/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Code Technology Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.dromara.jpom.service.user;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.keepbx.jpom.event.ISystemTask;
import lombok.extern.slf4j.Slf4j;
import org.dromara.jpom.model.PageResultDto;
import org.dromara.jpom.model.user.TriggerTokenLogBean;
import org.dromara.jpom.model.user.UserModel;
import org.dromara.jpom.service.ITriggerToken;
import org.dromara.jpom.service.h2db.BaseDbService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bwcx_jzy
 * @since 2022/7/22
 */
@Service
@Slf4j
public class TriggerTokenLogServer extends BaseDbService<TriggerTokenLogBean> implements ISystemTask {

    private final UserService userService;
    private final List<ITriggerToken> triggerTokens;

    public TriggerTokenLogServer(UserService userService,
                                 List<ITriggerToken> triggerTokens) {
        this.userService = userService;
        this.triggerTokens = triggerTokens;
    }

    /**
     * 通过用户ID 删除数据
     *
     * @param userId 用户d
     */
    public void delByUserId(String userId) {
        TriggerTokenLogBean tokenLogBean = new TriggerTokenLogBean();
        tokenLogBean.setUserId(userId);
        this.delByBean(tokenLogBean);
    }

    /**
     * 通过 token 获取用户ID
     *
     * @param token token
     * @param type  数据类型
     * @return user
     */
    public UserModel getUserByToken(String token, String type) {
        TriggerTokenLogBean tokenLogBean = super.getByKey(token);
        if (tokenLogBean != null) {
            UserModel userModel = userService.getByKey(tokenLogBean.getUserId());
            if (userModel != null && StrUtil.equals(type, tokenLogBean.getType())) {
                boolean demoUser = userModel.isDemoUser();
                Assert.state(!demoUser, "当前用户触发器不可用");
                return userModel;
            }
        }
        //
        return null;
    }

    /**
     * 重启生成 token
     *
     * @param oldToken 之前版本 token
     * @param type     类型
     * @param dataId   数据ID
     * @param userId   用户ID
     * @return 新 token
     */
    public String restToken(String oldToken, String type, String dataId, String userId) {
        if (StrUtil.isNotEmpty(oldToken)) {
            this.delByKey(oldToken);
        }
        // 创建 token
        return this.createToken(type, dataId, userId);
    }

    /**
     * 创建新 token
     *
     * @param type   类型
     * @param dataId 数据ID
     * @param userId 用户ID
     * @return token
     */
    private String createToken(String type, String dataId, String userId) {
        TriggerTokenLogBean trigger = new TriggerTokenLogBean();
        String uuid = IdUtil.fastSimpleUUID();
        trigger.setId(uuid);
        trigger.setTriggerToken(uuid);
        trigger.setType(type);
        trigger.setDataId(dataId);
        trigger.setUserId(userId);
        this.insert(trigger);
        return uuid;
    }

    @Override
    public void executeTask() {
        if (triggerTokens == null) {
            return;
        }
        log.debug("clean trigger token start...");
        long start = SystemClock.now();
        // 调用方法处理逻辑
        cleanTriggerToken();
        log.debug("clean trigger token end... cost time: {}", DateUtil.formatBetween(SystemClock.now() - start, BetweenFormatter.Level.MILLISECOND));
    }

    /**
     * @author Hotstrip
     * @since 2023-04-13
     */
    private void cleanTriggerToken() {
        // 统计删除条数
        int delCount = 0;
        for (ITriggerToken triggerToken : triggerTokens) {
            TriggerTokenLogBean tokenLogBean = new TriggerTokenLogBean();
            tokenLogBean.setType(triggerToken.typeName());
            try {
                int pageNumber = 1;
                while (true) {
                    Page page = new Page(pageNumber, 50);
                    Entity entity = new Entity();
                    entity.set("type", triggerToken.typeName());
                    entity.setFieldNames("id", "dataId");
                    PageResultDto<TriggerTokenLogBean> pageResult = this.listPage(entity, page);
                    if (pageResult.isEmpty()) {
                        break;
                    }
                    List<String> ids = new ArrayList<>();
                    List<TriggerTokenLogBean> result = pageResult.getResult();
                    for (TriggerTokenLogBean bean : result) {
                        //
                        String dataId = bean.getDataId();
                        if (triggerToken.exists(dataId)) {
                            continue;
                        }
                        String id = bean.getId();
                        ids.add(id);
                    }
                    // 删除 token
                    this.delByKey(ids);
                    if (pageResult.getTotalPage() <= pageNumber) {
                        break;
                    }
                    pageNumber++;
                    delCount += ids.size();
                }
            } catch (Exception e) {
                log.error("执行清理 token[{}] 异常", triggerToken.typeName(), e);
            }
        }
        if (delCount > 0) {
            log.info("clean trigger token count: {}", delCount);
        }
    }
}

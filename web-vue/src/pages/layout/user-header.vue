<template>
  <div class="user-header">
    <a-button-group>
      <!-- 工作空间信息 -->
      <a-button v-if="mode === 'normal'" type="dashed" class="workspace jpom-workspace btn-group-item">
        <div class="workspace-name">
          <a-tooltip :title="`工作空间名称：${selectWorkspace.name} 【分组名：${selectWorkspace.group || '未配置'}】`">
            <SwitcherOutlined />
            {{ selectWorkspace.name }}
            <template v-if="myClusterList.length > 1 && selectWorkspace.clusterInfoId">
              /
              {{
                myClusterList.find((item) => {
                  return item.id === selectWorkspace.clusterInfoId
                }) &&
                myClusterList.find((item) => {
                  return item.id === selectWorkspace.clusterInfoId
                }).name
              }}
            </template>
            <template v-if="!inClusterUrl">
              <SwapOutlined @click="handleClusterChange(selectCluster)" />
            </template>
          </a-tooltip>
        </div>
      </a-button>
      <a-button v-if="mode === 'management'" type="dashed">
        <div class="workspace-name">
          <a-tooltip :title="`集群名称：${selectCluster && selectCluster.name}`">
            <ClusterOutlined />
            {{ this.selectCluster && selectCluster.name }}
          </a-tooltip>
        </div>
      </a-button>
      <a-button type="primary" class="btn-group-item">
        <div class="user-name">
          <a-tooltip :title="getUserInfo.name"> {{ getUserInfo.name }} </a-tooltip>
        </div>
      </a-button>
      <a-dropdown>
        <a-button type="primary" class="btn-group-item"><DownOutlined /></a-button>
        <template #overlay>
          <a-menu>
            <!-- 工作空间信息 -->
            <template v-if="mode === 'normal'">
              <a-sub-menu>
                <template #title>
                  <a-button type="link"><RetweetOutlined />切换工作空间</a-button>
                </template>
                <template v-if="myWorkspaceList.length == 1">
                  <template v-for="(item, index) in myWorkspaceList[0].children">
                    <a-menu-item
                      v-if="index != -1"
                      :disabled="item.id === selectWorkspace.id"
                      @click="handleWorkspaceChange(item)"
                      :key="index"
                    >
                      <a-button type="link" :disabled="item.id === selectWorkspace.id">
                        {{ item.name || '未配置' }}
                        <template v-if="myClusterList.length > 1 && item.clusterInfoId">
                          【{{
                            myClusterList.find((item2) => {
                              return item2.id === item.clusterInfoId
                            }) &&
                            myClusterList.find((item2) => {
                              return item2.id === item.clusterInfoId
                            }).name
                          }}】
                        </template>
                      </a-button>
                    </a-menu-item>
                    <a-menu-divider v-if="index < myWorkspaceList[0].children.length - 1" :key="`${item.id}-divider`" />
                  </template>
                </template>
                <template v-if="myWorkspaceList.length > 1">
                  <template v-for="(item1, index1) in myWorkspaceList" :key="index1">
                    <a-sub-menu>
                      <template #title>
                        <a-button type="link">
                          {{ item1.value || '未配置' }}
                        </a-button>
                      </template>
                      <template v-for="(item, index) in item1.children">
                        <a-menu-item
                          v-if="index != -1"
                          :disabled="item.id === selectWorkspace.id"
                          @click="handleWorkspaceChange(item)"
                          :key="index"
                        >
                          <a-button type="link" :disabled="item.id === selectWorkspace.id">
                            {{ item.name }}
                            <template v-if="myClusterList.length > 1 && item.clusterInfoId">
                              【{{
                                myClusterList.find((item2) => {
                                  return item2.id === item.clusterInfoId
                                }) &&
                                myClusterList.find((item2) => {
                                  return item2.id === item.clusterInfoId
                                }).name
                              }}】
                            </template>
                          </a-button>
                        </a-menu-item>
                        <a-menu-divider v-if="index < item1.children.length - 1" :key="`${index1}-${index}-divider`" />
                      </template>
                    </a-sub-menu>
                    <a-menu-divider v-if="index1 < myWorkspaceList.length - 1" :key="`${index1}-divider`" />
                  </template>
                </template>
              </a-sub-menu>
              <a-menu-divider />
            </template>
            <!-- 集群信息 -->
            <template v-if="mode === 'management'">
              <a-sub-menu>
                <template #title>
                  <a-button type="link"><RetweetOutlined />切换集群</a-button>
                </template>
                <template v-for="(item, index) in myClusterList">
                  <a-menu-item
                    v-if="index != -1"
                    :disabled="item.id === selectCluster.id || !item.url"
                    @click="handleClusterChange(item)"
                    :key="index"
                  >
                    <a-button type="link" :disabled="item.id === selectCluster.id || !item.url">
                      {{ item.name }}
                    </a-button>
                  </a-menu-item>
                  <a-menu-divider v-if="index < myClusterList.length - 1" :key="`${item.id}-divider`" />
                </template>
              </a-sub-menu>
            </template>
            <a-menu-item @click="handleUpdatePwd">
              <a-button type="link"> <lock-outlined />安全管理 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="handleUpdateUser">
              <a-button type="link"><profile-outlined /> 用户资料 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="handleUserlog">
              <a-button type="link"><bars-outlined /> 操作日志 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="customize">
              <a-button type="link"><skin-outlined /> 个性配置 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="logOut">
              <a-button type="link"> <logout-outlined />退出登录 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="logOutSwap">
              <a-button type="link"> <SwapOutlined />切换账号 </a-button>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item @click="logOutAll">
              <a-button type="link"><RestOutlined /> 彻底退出 </a-button>
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </a-button-group>

    <!-- 修改密码区 -->
    <a-modal
      destroyOnClose
      v-model:open="updateNameVisible"
      :width="'60vw'"
      title="安全管理"
      :footer="null"
      :maskClosable="false"
    >
      <a-tabs v-model="temp.tabActiveKey" @change="tabChange">
        <a-tab-pane :key="1" tab="修改密码">
          <a-form
            ref="pwdForm"
            @finish="handleUpdatePwdOk"
            :rules="rules"
            :model="temp"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 14 }"
          >
            <a-form-item label="原密码" name="oldPwd">
              <a-input-password v-model:value="temp.oldPwd" placeholder="请输入原密码" />
            </a-form-item>
            <a-form-item label="新密码" name="newPwd">
              <a-input-password v-model:value="temp.newPwd" placeholder="请输入新密码" />
            </a-form-item>
            <a-form-item label="确认密码" name="confirmPwd">
              <a-input-password v-model:value="temp.confirmPwd" placeholder="请输入确认密码" />
            </a-form-item>
            <a-form-item>
              <a-row type="flex" justify="center">
                <a-col :span="2">
                  <a-button type="primary" html-type="submit">确认重置</a-button>
                </a-col>
              </a-row>
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane :key="2" tab="两步验证">
          <a-row>
            <a-col :span="24">
              <a-alert type="warning" v-if="temp.needVerify">
                <template #message> 提示 </template>
                <template #description>
                  <ul style="color: red">
                    <li>绑定成功后将不再显示,强烈建议保存此二维码或者下面的 MFA key</li>
                    <li>请使用应用扫码绑定令牌,然后输入验证码确认绑定才生效</li>
                  </ul>
                </template>
              </a-alert>
            </a-col>
            <a-col :span="12">
              <a-form
                ref="mfaForm"
                :model="temp"
                :rules="rules"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 14 }"
                @finish="handleBindMfa"
              >
                <a-form-item label="当前状态" name="status">
                  <a-switch
                    checked-children="开启中"
                    disabled
                    un-checked-children="关闭中"
                    v-model:checked="temp.status"
                  />
                </a-form-item>
                <template v-if="temp.needVerify">
                  <a-form-item label="二维码">
                    <a-row>
                      <a-col :span="14">
                        <a-qrcode :value="temp.url" :status="temp.url ? 'active' : 'loading'" />
                      </a-col>
                    </a-row>
                  </a-form-item>
                  <a-form-item label="MFA key">
                    <a-input readOnly disabled v-model:value="temp.mfaKey">
                      <template #suffix>
                        <a-typography-paragraph
                          style="margin-bottom: 0"
                          :copyable="{ tooltip: true, text: temp.mfaKey }"
                        >
                        </a-typography-paragraph>
                      </template>
                    </a-input>
                  </a-form-item>
                </template>
                <!-- 不能使用  template 包裹 否则验证不能正常启用 -->
                <a-form-item v-if="temp.needVerify" label="验证码" name="twoCode">
                  <a-input v-model:value="temp.twoCode" ref="twoCode" placeholder="两步验证码" />
                </a-form-item>
                <a-form-item v-if="temp.needVerify">
                  <a-row type="flex" justify="center">
                    <a-col :span="2">
                      <a-button type="primary" html-type="submit">确认绑定</a-button>
                    </a-col>
                  </a-row>
                </a-form-item>
                <!-- 不能使用  template 包裹 否则验证不能正常启用 -->
                <a-form-item v-if="!temp.needVerify && temp.status" label="验证码" name="twoCode">
                  <a-input v-model:value="temp.twoCode" ref="twoCode" placeholder="两步验证码" />
                </a-form-item>
                <a-form-item v-if="!temp.needVerify && temp.status">
                  <a-row type="flex" justify="center">
                    <a-col :span="2">
                      <a-button type="primary" html-type="submit">确认关闭</a-button>
                    </a-col>
                  </a-row>
                </a-form-item>

                <a-form-item v-if="!temp.needVerify && !temp.status">
                  <a-row type="flex" justify="center">
                    <a-col :span="2">
                      <a-button type="primary" @click="openMfaFn">开启 MFA</a-button>
                    </a-col>
                  </a-row>
                </a-form-item>
              </a-form>
            </a-col>
            <a-col :span="12">
              <h3 id="两步验证应用">两步验证应用</h3>
              <p v-for="(html, index) in MFA_APP_TIP_ARRAY" :key="index" v-html="html" />
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
    <!-- 修改用户资料区 -->
    <a-modal
      destroyOnClose
      :confirmLoading="confirmLoading"
      v-model:open="updateUserVisible"
      title="修改用户资料"
      @ok="handleUpdateUserOk"
      :maskClosable="false"
    >
      <a-form ref="userForm" :rules="rules" :model="temp" :label-col="{ span: 8 }" :wrapper-col="{ span: 15 }">
        <a-form-item label="临时token" name="token">
          <a-input disabled v-model:value="temp.token" placeholder="Token">
            <template #suffix>
              <a-typography-paragraph style="margin-bottom: 0" :copyable="{ tooltip: true, text: temp.token }">
              </a-typography-paragraph>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="长期token" name="md5Token">
          <a-input disabled v-model:value="temp.md5Token" placeholder="Token">
            <template #suffix>
              <a-typography-paragraph style="margin-bottom: 0" :copyable="{ tooltip: true, text: temp.md5Token }">
              </a-typography-paragraph>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="昵称" name="name">
          <a-input v-model:value="temp.name" placeholder="昵称" />
        </a-form-item>
        <a-form-item label="邮箱地址" name="email">
          <a-input v-model:value="temp.email" placeholder="邮箱地址" />
        </a-form-item>
        <a-form-item v-show="showCode" label="邮箱验证码" name="code">
          <a-row :gutter="8">
            <a-col :span="15">
              <a-input v-model:value="temp.code" placeholder="邮箱验证码" />
            </a-col>
            <a-col :span="4">
              <a-button type="primary" :disabled="!temp.email" @click="sendEmailCode">发送验证码</a-button>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item label="钉钉通知地址" name="dingDing">
          <a-input v-model:value="temp.dingDing" placeholder="钉钉通知地址" />
        </a-form-item>
        <a-form-item label="企业微信通知地址" name="workWx">
          <a-input v-model:value="temp.workWx" placeholder="企业微信通知地址" />
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- 个性配置区 -->
    <a-modal destroyOnClose v-model:open="customizeVisible" title="个性配置区" :footer="null" :maskClosable="false">
      <a-form :model="temp" :label-col="{ span: 6 }" :wrapper-col="{ span: 14 }">
        <a-alert banner>
          <template #message> 下列配置信息仅在当前浏览器生效,清空浏览器缓存配置将恢复默认 </template>
        </a-alert>
        <!-- <a-form-item label="页面导航">
          <a-space>
            <a-switch
              checked-children="开"
              @click="toggleGuide"
              :checked="!this.guideStatus"
              :disabled="this.getDisabledGuide"
              un-checked-children="关"
            />

            <div v-if="!this.guideStatus">
              重置导航
              <RestOutlined @click="restGuide" />
            </div>
          </a-space>
        </a-form-item> -->
        <a-form-item label="菜单配置">
          <a-space>
            <a-switch
              checked-children="是"
              @click="toggleMenuMultiple"
              :checked="this.menuMultipleFlag"
              un-checked-children="否"
            />
            同时展开多个
          </a-space>
        </a-form-item>
        <!-- <a-form-item label="页面配置">
          <a-space>
            自动撑开：
            <a-switch
              checked-children="是"
              @click="toggleFullScreenFlag"
              :checked="this.fullScreenFlag"
              un-checked-children="否"
            />
          </a-space>
        </a-form-item>
        <a-form-item label="滚动条显示">
          <a-space>
            全局配置：
            <a-switch
              checked-children="显示"
              @click="toggleScrollbarFlag"
              :checked="this.scrollbarFlag"
              un-checked-children="不显示"
            />
          </a-space>
        </a-form-item> -->
        <a-form-item label="全屏日志">
          <a-space>
            <a-switch
              checked-children="全屏"
              @click="toggleFullscreenViewLog"
              :checked="this.fullscreenViewLog"
              un-checked-children="非全屏"
            />
            全屏查看日志
          </a-space>
        </a-form-item>
        <a-form-item label="内容主题">
          <a-space>
            <a-radio-group v-model:value="themeView" button-style="solid">
              <a-radio-button value="light">浅色</a-radio-button>
              <a-radio-button value="dark">深色</a-radio-button>
            </a-radio-group>
            内容区域主题切换
          </a-space>
        </a-form-item>
        <a-form-item label="菜单主题">
          <a-space>
            <a-radio-group v-model:value="menuThemeView" button-style="solid">
              <a-radio-button value="light">浅色</a-radio-button>
              <a-radio-button value="dark">深色</a-radio-button>
            </a-radio-group>
            左边菜单栏主题切换
          </a-space>
        </a-form-item>

        <a-form-item label="紧凑模式">
          <a-space>
            <a-switch
              checked-children="紧凑"
              @click="toggleCompactView"
              :checked="this.compactView"
              un-checked-children="宽松"
            />
            字体间距调整(仅在深色模式生效)
          </a-space>
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- mfa 提示 -->
    <a-modal
      destroyOnClose
      v-model:open="bindMfaTip"
      title="安全提醒"
      :footer="null"
      :maskClosable="false"
      :closable="false"
      :keyboard="false"
    >
      <a-space direction="vertical">
        <a-alert
          message="安全提醒"
          description="为了您的账号安全系统要求必须开启两步验证来确保账号的安全性"
          type="error"
          :closable="false"
        />
        <a-row align="middle" type="flex" justify="center">
          <a-button type="danger" @click="toBindMfa"> 立即开启 </a-button>
        </a-row>
      </a-space>
    </a-modal>
    <!-- 查看操作日志 -->
    <a-modal
      destroyOnClose
      v-model:open="viewLogVisible"
      :width="'90vw'"
      title="操作日志"
      :footer="null"
      :maskClosable="false"
    >
      <user-log v-if="viewLogVisible"></user-log>
    </a-modal>
  </div>
</template>
<script>
import {
  bindMfa,
  closeMfa,
  editUserInfo,
  generateMfa,
  getUserInfo,
  myWorkspace,
  sendEmailCode,
  updatePwd,
  clusterList
} from '@/api/user/user'

import sha1 from 'js-sha1'
// import Vue from 'vue'
import { MFA_APP_TIP_ARRAY, itemGroupBy } from '@/utils/const'
import UserLog from './user-log.vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { useGuideStore } from '@/stores/guide'

import { useAllMenuStore } from '@/stores/menu2'
export default {
  components: {
    UserLog
  },
  props: {
    mode: {
      type: String
    }
  },
  data() {
    return {
      collapsed: false,
      // 修改密码框
      updateNameVisible: false,
      updateUserVisible: false,
      temp: {},

      myWorkspaceList: [],
      myClusterList: [],
      currentClusterId: '',
      selectWorkspace: {},
      customizeVisible: false,
      // 表单校验规则
      rules: {
        name: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { max: 10, message: '昵称长度为2-10', trigger: 'blur' },
          { min: 2, message: '昵称长度为2-10', trigger: 'blur' }
        ],
        oldPwd: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          { max: 20, message: '密码长度为6-20', trigger: 'blur' },
          { min: 6, message: '密码长度为6-20', trigger: 'blur' }
        ],
        newPwd: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { max: 20, message: '密码长度为6-20', trigger: 'blur' },
          { min: 6, message: '密码长度为6-20', trigger: 'blur' }
        ],
        confirmPwd: [
          { required: true, message: '请输入确认密码', trigger: 'blur' },
          { max: 20, message: '密码长度为6-20', trigger: 'blur' },
          { min: 6, message: '密码长度为6-20', trigger: 'blur' }
        ],
        email: [
          // { required: true, message: "请输入邮箱", trigger: "blur" }
        ],
        twoCode: [
          { required: true, message: '请输入两步验证码', trigger: ['change', 'blur'] },
          { pattern: /^\d{6}$/, message: '验证码 6 为纯数字', trigger: ['change', 'blur'] }
        ]
      },
      MFA_APP_TIP_ARRAY,
      bindMfaTip: false,
      viewLogVisible: false,
      confirmLoading: false
    }
  },
  computed: {
    ...mapState(useUserStore, ['getToken', 'getUserInfo']),
    ...mapState(useAppStore, ['getWorkspaceId']),
    ...mapState(useGuideStore, ['getGuideCache', 'getDisabledGuide', 'getThemeView', 'getMenuThemeView']),

    showCode() {
      return this.getUserInfo.email !== this.temp.email
    },
    guideStatus() {
      return this.getGuideCache.close
    },
    menuMultipleFlag() {
      return this.getGuideCache.menuMultipleFlag === undefined ? true : this.getGuideCache.menuMultipleFlag
    },
    fullScreenFlag() {
      return this.getGuideCache.fullScreenFlag === undefined ? true : this.getGuideCache.fullScreenFlag
    },
    scrollbarFlag() {
      return this.getGuideCache.scrollbarFlag === undefined ? true : this.getGuideCache.scrollbarFlag
    },
    compactView() {
      return this.getGuideCache.compactView === undefined ? false : this.getGuideCache.compactView
    },
    themeView: {
      set: function (value) {
        useGuideStore().toggleThemeView(value)
      },
      get: function () {
        return this.getThemeView()
      }
    },
    menuThemeView: {
      set: function (value) {
        useGuideStore().toggleMenuThemeView(value)
      },
      get: function () {
        return this.getMenuThemeView()
      }
    },
    fullscreenViewLog() {
      return !!this.getGuideCache.fullscreenViewLog
    },
    selectCluster: {
      get: function () {
        const temp = this.myClusterList.find((item) => {
          return item.id === this.currentClusterId
        })
        return temp
      }
    },
    inClusterUrl() {
      const data = this.selectCluster
      if (!data || !data.url) {
        // 没有配置集群地址
        return true
      }
      return window.location.href.indexOf(data && data.url) === 0
    }
  },
  inject: ['reload'],
  created() {
    this.init()
  },
  methods: {
    customize() {
      this.customizeVisible = true
    },

    init() {
      if (this.mode === 'normal') {
        // 获取工作空间
        myWorkspace().then((res) => {
          if (res.code == 200 && res.data) {
            const tempArray = res.data

            this.myWorkspaceList = itemGroupBy(tempArray, 'group', 'value', 'children')

            let wid = this.$route.query.wid
            wid = wid ? wid : this.getWorkspaceId()
            const existWorkspace = tempArray.find((item) => item.id === wid)

            if (existWorkspace) {
              this.$router.push({
                query: { ...this.$route.query, wid: wid }
              })
              this.selectWorkspace = existWorkspace
            } else {
              this.handleWorkspaceChange(res.data[0])
            }
          }
        })
      }
      // 获取集群
      clusterList().then((res) => {
        if (res.code == 200 && res.data) {
          this.myClusterList = res.data.list || []
          this.currentClusterId = res.data.currentId
        }
      })
      this.checkMfa()
    },
    checkMfa() {
      if (!this.getUserInfo) {
        return
      }
      if (this.getUserInfo.forceMfa === true && this.getUserInfo.bindMfa === false) {
        this.bindMfaTip = true
      }
    },
    toBindMfa() {
      this.bindMfaTip = false
      this.updateNameVisible = true
      this.tabChange(2)
    },
    // 切换引导
    toggleGuide() {
      useGuideStore()
        .toggleGuideFlag()
        .then((flag) => {
          if (flag) {
            $notification.success({
              message: '关闭页面操作引导、导航'
            })
          } else {
            $notification.success({
              message: '开启页面操作引导、导航'
            })
          }
        })
    },
    // 切换菜单打开
    toggleMenuMultiple() {
      useGuideStore()
        .toggleMenuFlag()
        .then((flag) => {
          if (flag) {
            $notification.success({
              message: '可以同时展开多个菜单'
            })
          } else {
            $notification.success({
              message: '同时只能展开一个菜单'
            })
          }
        })
    },
    // 页面全屏
    toggleFullScreenFlag() {
      useGuideStore()
        .toggleFullScreenFlag()
        .then((flag) => {
          if (flag) {
            $notification.success({
              message: '页面内容自动撑开出现屏幕滚动条'
            })
          } else {
            $notification.success({
              message: '页面全屏，高度 100%。局部区域可以滚动'
            })
          }
        })
    },
    // 切换滚动条是否显示
    toggleScrollbarFlag() {
      useGuideStore()
        .toggleScrollbarFlag()
        .then((flag) => {
          if (flag) {
            $notification.success({
              message: '页面内容会出现滚动条'
            })
          } else {
            $notification.success({
              message: '隐藏滚动条。纵向滚动方式提醒：滚轮，横行滚动方式：Shift+滚轮'
            })
          }
        })
    },
    // 切换全屏查看日志
    toggleFullscreenViewLog() {
      useGuideStore()
        .toggleFullscreenViewLog()
        .then((fullscreenViewLog) => {
          if (fullscreenViewLog) {
            $notification.success({
              message: '日志弹窗会全屏打开'
            })
          } else {
            $notification.success({
              message: '日志弹窗会非全屏打开'
            })
          }
        })
    },
    toggleCompactView() {
      useGuideStore()
        .toggleCompactView()
        .then((compact) => {
          if (compact) {
            $notification.success({
              message: '页面启用紧凑模式'
            })
          } else {
            $notification.success({
              message: '页面启用宽松模式'
            })
          }
        })
    },
    restGuide() {
      useGuideStore()
        .restGuide()
        .then(() => {
          $notification.success({
            message: '重置页面操作引导、导航成功'
          })
        })
    },
    // 彻底退出登录
    logOutAll() {
      const that = this
      $confirm({
        title: '系统提示',
        zIndex: 1009,
        content: '真的要彻底退出系统么？彻底退出将退出登录和清空浏览器缓存',
        okText: '确认',
        cancelText: '取消',
        async onOk() {
          return await new Promise((resolve, reject) => {
            // 退出登录
            useUserStore()
              .logOut()
              .then(() => {
                $notification.success({
                  message: '退出登录成功'
                })
                localStorage.clear()
                that.$router.replace({
                  path: '/login',
                  query: {}
                })
                resolve()
              })
              .catch(reject)
          })
        }
      })
    },
    // 切换账号登录
    logOutSwap() {
      const that = this
      this.$confirm({
        title: '系统提示',
        zIndex: 1009,
        content: '真的要退出并切换账号登录么？',
        okText: '确认',
        cancelText: '取消',
        async onOk() {
          return await new Promise((resolve, reject) => {
            // 退出登录
            useUserStore()
              .logOut()
              .then(() => {
                $notification.success({
                  message: '退出登录成功'
                })
                useAppStore().changeWorkspace('')
                that.$router.replace({
                  path: '/login',
                  query: {}
                })
                resolve()
              })
              .catch(reject)
          })
        }
      })
    },
    // 退出登录
    logOut() {
      this.$confirm({
        title: '系统提示',
        zIndex: 1009,
        content: '真的要退出系统么？',
        okText: '确认',
        cancelText: '取消',
        async onOk() {
          return await new Promise((resolve, reject) => {
            // 退出登录
            useUserStore()
              .logOut()
              .then(() => {
                $notification.success({
                  message: '退出登录成功'
                })
                const query = Object.assign({}, $route.query)
                $router.replace({
                  path: '/login',
                  query: query
                })
                resolve()
              })
              .catch(reject)
          })
        }
      })
    },
    // 加载修改密码对话框
    handleUpdatePwd() {
      this.updateNameVisible = true
      this.tabChange(1)
    },
    // 修改密码
    handleUpdatePwdOk() {
      // 判断两次新密码是否一致
      if (this.temp.newPwd !== this.temp.confirmPwd) {
        $notification.error({
          message: '两次密码不一致...'
        })
        return
      }
      // 提交修改
      const params = {
        oldPwd: sha1(this.temp.oldPwd),
        newPwd: sha1(this.temp.newPwd)
      }
      updatePwd(params).then((res) => {
        // 修改成功
        if (res.code === 200) {
          // 退出登录
          userStore()
            .logOut()
            .then(() => {
              $notification.success({
                message: res.msg
              })

              this.updateNameVisible = false
              this.$router.push('/login')
            })
        }
      })
      // })
    },
    // 加载修改用户资料对话框
    handleUpdateUser() {
      getUserInfo().then((res) => {
        if (res.code === 200) {
          this.temp = res.data
          this.temp.token = this.getToken
          //this.temp.md5Token = res.data.md5Token;
          this.updateUserVisible = true
        }
      })
    },
    // 发送邮箱验证码
    sendEmailCode() {
      if (!this.temp.email) {
        $notification.error({
          message: '请输入邮箱地址'
        })
        return
      }
      sendEmailCode(this.temp.email).then((res) => {
        if (res.code === 200) {
          $notification.success({
            message: res.msg
          })
        }
      })
    },
    // 修改用户资料
    handleUpdateUserOk() {
      // 检验表单
      this.$refs['userForm'].validate().then(() => {
        const tempData = Object.assign({}, this.temp)
        delete tempData.token, delete tempData.md5Token
        this.confirmLoading = true
        editUserInfo(tempData)
          .then((res) => {
            // 修改成功
            if (res.code === 200) {
              $notification.success({
                message: res.msg
              })
              // 清空表单校验
              this.$refs['userForm'].resetFields()
              this.updateUserVisible = false
              userStore().refreshUserInfo()
            }
          })
          .finally(() => {
            this.confirmLoading = false
          })
      })
    },
    // 工作空间切换
    handleWorkspaceChange(item) {
      const cluster = this.myClusterList.find((item2) => {
        return item2.id === item.clusterInfoId
      })
      if (cluster && cluster.url && location.href.indexOf(cluster.url) !== 0) {
        let url = `${cluster.url}/#/${this.$route.fullPath}`.replace(/[\\/]+[\\/]/g, '/').replace(':/', '://')
        url = url.replace(`wid=${this.selectWorkspace.id}`, `wid=${item.id}`)
        // console.log(location.href.indexOf(cluster.url), url);
        location.href = url
      } else {
        appStore()
          .changeWorkspace(item.id)
          .then(() => {
            this.$router
              .push({
                query: { ...this.$route.query, wid: item.id }
              })
              .then(() => {
                useAllMenuStore().restLoadSystemMenus('normal')
                this.reload()
              })
          })
      }
    },
    // 集群切换
    handleClusterChange(item) {
      if (item.url) {
        const url = `${item.url}/#/${this.$route.fullPath}`.replace(/[\\/]+[\\/]/g, '/').replace(':/', '://')
        // console.log(url);
        location.href = url
      } else {
        $notificationr({
          message: '还未配置集群地址,不能切换集群'
        })
      }
    },
    tabChange(key) {
      if (key === 1) {
        this.temp = { tabActiveKey: key }
      } else if (key == 2) {
        this.temp = { tabActiveKey: key }
        getUserInfo().then((res) => {
          if (res.code === 200) {
            this.temp = { ...this.temp, status: res.data.bindMfa }
            this.$nextTick(() => {
              this.$refs?.twoCode?.focus()
            })
          }
        })
      }
    },

    // 关闭 mfa

    // mfa 状态切换
    openMfaFn() {
      //
      generateMfa().then((res) => {
        if (res.code === 200) {
          Object.assign(this.temp, {
            status: true,
            mfaKey: res.data.mfaKey,
            url: res.data.url,
            needVerify: true,
            showSaveTip: true,
            twoCode: ''
          })
          this.temp = { ...this.temp }

          $notification.info({
            // placement: "",
            message: '需要输入验证码,确认绑定后才生效奥'
          })
        }
      })
    },
    handleBindMfa() {
      // @click="closeMfaFn"
      if (this.temp.needVerify) {
        bindMfa({
          mfa: this.temp.mfaKey,
          twoCode: this.temp.twoCode
        }).then((res) => {
          if (res.code === 200) {
            $notification.success({
              message: res.msg
            })
            this.temp = { ...this.temp, needVerify: false, twoCode: '' }
          }
        })
      } else {
        const that = this
        $confirm({
          title: '系统提示',
          zIndex: 1009,
          content: '确定要关闭两步验证吗？关闭后账号安全性将受到影响,关闭后已经存在的 mfa key 将失效',
          okText: '确认',
          cancelText: '取消',
          async onOk() {
            return await new Promise((resolve, reject) => {
              //
              closeMfa({
                code: that.temp.twoCode
              })
                .then((res) => {
                  if (res.code === 200) {
                    $notification.success({
                      message: res.msg
                    })
                    that.temp = { ...that.temp, needVerify: false, status: false }
                  }
                  resolve()
                })
                .catch(reject)
            })
          }
        })
      }
    },
    handleUserlog() {
      this.viewLogVisible = true
    }
  }
}
</script>
<style scoped>
.btn-group-item {
  padding: 0 5px;
}
.workspace-name {
  min-width: 30px;
  max-width: 200px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-weight: bold;
}

.user-name {
  min-width: 30px;
  max-width: 100px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>

<template>
  <div class="">
    <a-tabs default-active-key="1" tab-position="left">
      <a-tab-pane key="1" tab="缓存信息">
        <a-alert message="请勿手动删除数据目录下面文件,如果需要删除需要提前备份或者已经确定对应文件弃用后才能删除" style="margin-top: 10px; margin-bottom: 40px" banner />
        <a-timeline>
          <a-timeline-item v-if="temp.dateTime">
            <span class="layui-elem-quote">
              插件端时间：{{ temp.dateTime }} <a-tag>{{ temp.timeZoneId }}</a-tag>
            </span>
          </a-timeline-item>
          <a-timeline-item>
            <span class="layui-elem-quote">数据目录占用空间：{{ renderSize(temp.dataSize) }}</span>
          </a-timeline-item>
          <a-timeline-item v-if="temp.fileSize">
            <a-space>
              <span class="layui-elem-quote">临时文件占用空间：{{ renderSize(temp.fileSize) }}</span>
              <a-button size="small" type="primary" class="btn" @click="clear('fileSize')">清空</a-button>
            </a-space>
          </a-timeline-item>
          <a-timeline-item v-if="temp.oldJarsSize">
            <a-space>
              <span class="layui-elem-quote">旧版程序包占有空间：{{ renderSize(temp.oldJarsSize) }}</span>
              <a-button size="small" type="primary" class="btn" @click="clear('oldJarsSize')">清空</a-button>
            </a-space>
          </a-timeline-item>

          <a-timeline-item>
            <a-space>
              <span class="layui-elem-quote">进程端口缓存：{{ temp.pidPort }}</span>
              <a-button size="small" v-if="temp.pidPort" type="primary" class="btn" @click="clear('pidPort')">清空</a-button>
            </a-space>
          </a-timeline-item>
          <!-- <a-timeline-item>
        <span class="layui-elem-quote">错误进程缓存：{{temp.pidError}}</span>
        <a-button type="primary" class="btn" @click="clear('pidError')">清空</a-button>
      </a-timeline-item> -->
          <a-timeline-item>
            <span class="layui-elem-quote">在读取的日志文件数：{{ temp.readFileOnLineCount }}</span>
          </a-timeline-item>
          <a-timeline-item>
            <span class="layui-elem-quote">插件数：{{ temp.pluginSize || 0 }}</span>
          </a-timeline-item>
          <a-timeline-item>
            <div class="layui-elem-quote">
              环境变量：
              <a-tag v-for="(item, index) in temp.envVarKeys" :key="index">
                <a-tooltip :title="`环境变量的key:${item}`">
                  {{ item }}
                </a-tooltip>
              </a-tag>
            </div>
          </a-timeline-item>
        </a-timeline>
      </a-tab-pane>
      <a-tab-pane key="2" tab="定时任务"> <task-stat :taskList="taskList" @refresh="loadData" /></a-tab-pane>
    </a-tabs>
  </div>
</template>
<script>
import { getNodeCache, clearCache } from "@/api/system";
import TaskStat from "@/pages/system/taskStat";
import { renderSize } from "@/utils/const";
export default {
  components: {
    TaskStat,
  },
  props: {
    machineId: {
      type: String,
    },
  },
  data() {
    return {
      temp: {},
      taskList: [],
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    // parseTime,
    renderSize,
    // load data
    loadData() {
      getNodeCache({
        machineId: this.machineId,
      }).then((res) => {
        if (res.code === 200) {
          this.temp = res.data;
          this.taskList = res.data?.taskList;
        }
      });
    },
    // clear
    clear(type) {
      const params = {
        type: type,
        machineId: this.machineId,
      };
      clearCache(params).then((res) => {
        if (res.code === 200) {
          // 成功
          this.$notification.success({
            message: res.msg,
          });
          this.loadData();
        }
      });
    },
  },
};
</script>

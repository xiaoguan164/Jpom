<template>
  <a-modal
    destroyOnClose
    :width="getFullscreenViewLogStyle.width"
    :dialogStyle="getFullscreenViewLogStyle.dialogStyle"
    :bodyStyle="getFullscreenViewLogStyle.bodyStyle"
    v-model="visibleModel"
    :footer="null"
    :maskClosable="false"
    @cancel="close"
  >
    <template #title>
      <template>
        <a-page-header :title="titleName" :backIcon="false">
          <template #subTitle>
            <a-row type="flex" align="middle">
              <a-col>
                <slot name="before"></slot>
              </a-col>

              <a-col v-if="extendBar" style="padding-left: 10px">
                <a-space>
                  <a-tooltip title="清空当前缓冲区内容">
                    <a-button type="primary" size="small" @click="clearLogCache" icon="delete">清空</a-button>
                  </a-tooltip>
                  <!-- <a-tooltip title="内容超过边界自动换行">
                <a-switch v-model="temp.wordBreak" checked-children="自动换行" un-checked-children="不换行" @change="onChange" />
              </a-tooltip> -->
                  <a-tooltip title="有新内容后是否自动滚动到底部">
                    <a-switch v-model="temp.logScroll" checked-children="自动滚动" un-checked-children="不滚动" @change="onChange" />
                  </a-tooltip>
                  <!-- <a-dropdown>
                <a-button type="link" style="padding: 0" icon="setting"> 设置 <a-icon type="down" /></a-button>
                <a-menu slot="overlay">
                  <a-menu-item key="0"> </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="3"> </a-menu-item>
                </a-menu>
              </a-dropdown> -->
                </a-space>
              </a-col>
            </a-row>
          </template>
        </a-page-header>
      </template>
    </template>

    <viewPre ref="viewPre" :height="`calc(${getFullscreenViewLogStyle.bodyStyle.height} - 50px)`" :config="this.temp"></viewPre>
  </a-modal>
</template>

<script>
import viewPre from "./view-pre";
import { mapGetters } from "vuex";
export default {
  name: "LogView",
  components: {
    viewPre,
    // VNodes: {
    //   functional: true,
    //   render: (h, ctx) => ctx.props.vnodes,
    // },
  },
  computed: {
    ...mapGetters(["getFullscreenViewLogStyle"]),
    regModifier() {
      return this.regModifiers.join("");
    },
  },
  props: {
    titleName: {
      String,
      default: "",
    },
    marginTop: {
      String,
      default: "0",
    },
    extendBar: {
      Boolean,
      default: true,
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      temp: {
        logScroll: true,
        // 自动换行
        wordBreak: false,
      },
      visibleModel: false,
    };
  },
  created() {
    this.visibleModel = this.visible;
  },
  mounted() {
    const cacheJson = localStorage.getItem("log-view-cache") || "{}";
    try {
      const cacheData = JSON.parse(cacheJson);
      this.temp = Object.assign({}, this.temp, cacheData);
    } catch (e) {
      console.error(e);
    }
  },
  methods: {
    appendLine(data) {
      this.$refs.viewPre?.appendLine(data);
    },
    clearLogCache() {
      this.$refs.viewPre?.clearLogCache();
    },
    onChange() {
      localStorage.setItem("log-view-cache", JSON.stringify(this.temp));
    },
    close() {
      this.visibleModel = false;
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.log-filter {
  /* margin-top: -22px; */
  /* margin-bottom: 10px; */
  padding: 0 10px;
  padding-top: 0;
  padding-bottom: 10px;
  line-height: 0;
}

/deep/ .ant-checkbox-group-item {
  display: flex;
  align-items: center;
}

/deep/ .ant-page-header {
  padding: 0;
}
</style>

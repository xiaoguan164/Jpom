<template>
  <a-drawer
    destroyOnClose
    placement="right"
    :width="`${this.getCollapsed ? 'calc(100vw - 80px)' : 'calc(100vw - 200px)'}`"
    :open="true"
    @close="onClose1"
    :bodyStyle="{
      padding: '0'
    }"
    :headerStyle="{
      padding: '0 10px'
    }"
  >
    <template #title>
      <!-- 集群控制台 -->
      <a-menu mode="horizontal" class="docker-menu" v-model:selectedKeys="menuKeyArray" @click="menuClick">
        <a-menu-item key="node">
          <span class="nav-text">集群节点</span>
        </a-menu-item>
        <a-menu-item key="server">
          <span class="nav-text">集群服务</span>
        </a-menu-item>
        <a-menu-item key="task">
          <span class="nav-text">集群任务</span>
        </a-menu-item>
      </a-menu></template
    >

    <!-- <a-layout-header style="height: 48px; padding: 0"> </a-layout-header> -->

    <div class="layout-content">
      <swarm-node v-if="menuKey === 'node'" :id="this.id" :visible="this.visible" :urlPrefix="this.urlPrefix" />
      <swarm-service v-if="menuKey === 'server'" :id="this.id" :visible="this.visible" :urlPrefix="this.urlPrefix" />
      <swarm-task v-if="menuKey === 'task'" :id="this.id" :visible="this.visible" :urlPrefix="this.urlPrefix" />
    </div>
  </a-drawer>
</template>

<script>
import SwarmNode from './node'
import SwarmService from './service'
import SwarmTask from './task'
import { mapState } from 'pinia'
import { useGuideStore } from '@/stores/guide'
export default {
  props: {
    id: {
      type: String
    },
    initMenu: {
      type: String,
      default: 'node'
    },
    visible: {
      type: Boolean,
      default: false
    },
    urlPrefix: {
      type: String
    }
  },
  components: {
    SwarmNode,
    SwarmService,
    SwarmTask
  },
  data() {
    return {
      menuKeyArray: [],
      menuKey: ''
    }
  },
  computed: {
    ...mapState(useGuideStore, ['getCollapsed'])
  },
  mounted() {
    this.menuKey = this.initMenu
    this.menuKeyArray = [this.initMenu]
  },
  methods: {
    menuClick(item) {
      this.menuKey = item.key
    },
    onClose1() {
      this.$emit('close')
    }
  },
  emits: ['close']
}
</script>

<style scoped>
.docker-menu {
  border-bottom: 0;
}
.layout-content {
  padding: 0;
  margin: 15px;
}
</style>

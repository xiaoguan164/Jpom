<template>
  <div>
    <log-view1 :ref="`logView`" height="calc(100vh - 140px)">
      <template v-slot:before> <a-button type="primary" size="small" @click="goFile">文件管理</a-button></template>
    </log-view1>
  </div>
</template>

<script>
// import { getProjectData, getProjectLogSize, downloadProjectLogFile, getLogBackList, downloadProjectLogBackFile, deleteProjectLogBackFile } from "@/api/node-project";
import { getWebSocketUrl } from '@/api/config'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import LogView1 from '@/components/logView/index2'

export default {
  components: {
    LogView1
  },
  props: {
    nodeId: {
      type: String
    },
    projectId: {
      type: String
    },
    id: {
      type: String
    },
    readFilePath: {
      type: String
    }
  },
  data() {
    return {
      project: {},
      optButtonLoading: true,
      loading: false,
      socket: null,

      heart: null
    }
  },
  computed: {
    ...mapState(useUserStore, ['getLongTermToken']),
    ...mapState(useAppStore, ['getWorkspaceId']),
    socketUrl() {
      return getWebSocketUrl(
        '/socket/console',
        `userId=${this.getLongTermToken}&id=${this.id}&nodeId=${
          this.nodeId
        }&type=console&workspaceId=${this.getWorkspaceId()}`
      )
    }
  },
  mounted() {
    // this.loadProject();
    this.initWebSocket()
    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = () => {
      this.close()
    }
  },
  beforeUnmount() {
    this.close()
  },
  methods: {
    close() {
      this.socket?.close()

      clearInterval(this.heart)
    },
    // 初始化
    initWebSocket() {
      //this.logContext = "";
      if (
        !this.socket ||
        this.socket.readyState !== this.socket.OPEN ||
        this.socket.readyState !== this.socket.CONNECTING
      ) {
        this.socket = new WebSocket(this.socketUrl)
      }
      // 连接成功后
      this.socket.onopen = () => {
        this.sendMsg('showlog')
      }
      this.socket.onerror = (err) => {
        console.error(err)
        $notification.error({
          message: 'web socket 错误,请检查是否开启 ws 代理'
        })
        clearInterval(this.heart)
      }
      this.socket.onclose = (err) => {
        //当客户端收到服务端发送的关闭连接请求时，触发onclose事件
        console.error(err)
        this.$message.warning('会话已经关闭[tail-file]')
        clearInterval(this.heart)
      }
      this.socket.onmessage = (msg) => {
        this.$refs.logView?.appendLine(msg.data)

        clearInterval(this.heart)
        // 创建心跳，防止掉线
        this.heart = setInterval(() => {
          this.sendMsg('heart')
          // this.loadFileSize();
        }, 5000)
      }
    },

    // 发送消息
    sendMsg(op) {
      const data = {
        op: op,
        projectId: this.projectId,
        fileName: this.readFilePath
      }
      this.socket.send(JSON.stringify(data))
    },

    goFile() {
      this.$emit('goFile')
    }
  },
  emits: ['goFile']
}
</script>

<style scoped>
.filter {
  margin: 0 0 10px;
}
</style>

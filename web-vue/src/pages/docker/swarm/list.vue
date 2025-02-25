<template>
  <div>
    <template v-if="this.useSuggestions">
      <a-result
        title="当前工作空间还没有 Docker 集群"
        sub-title="请到【系统管理】-> 【资产管理】-> 【Docker管理】新增Docker并创建集群，或者将已存在的的 Docker 集群授权关联、分配到此工作空间"
      >
        <template #extra>
          <router-link to="/system/assets/docker-list">
            <a-button key="console" type="primary">现在就去</a-button></router-link
          >
        </template>
      </a-result>
    </template>
    <a-table
      v-else
      size="middle"
      :data-source="list"
      :columns="columns"
      @change="changePage"
      :pagination="pagination"
      bordered
      :scroll="{
        x: 'max-content'
      }"
    >
      <template v-slot:title>
        <a-space>
          <a-input
            v-model:value="listQuery['%name%']"
            @pressEnter="loadData"
            placeholder="名称"
            class="search-input-item"
          />
          <a-input
            v-model:value="listQuery['%tag%']"
            @pressEnter="loadData"
            placeholder="标签"
            class="search-input-item"
          />
          <a-tooltip title="按住 Ctr 或者 Alt/Option 键点击按钮快速回到第一页">
            <a-button type="primary" @click="loadData" :loading="loading">搜索</a-button>
          </a-tooltip>
        </a-space>
      </template>

      <template #bodyCell="{ column, text, record }">
        <template v-if="column.tooltip">
          <a-tooltip placement="topLeft" :title="text">
            <span>{{ text }}</span>
          </a-tooltip>
        </template>
        <template v-else-if="column.dataIndex instanceof Array && column.dataIndex.includes('status')">
          <template v-if="record.machineDocker">
            <a-tag color="green" v-if="record.machineDocker.status === 1">正常</a-tag>
            <a-tooltip v-else :title="record.machineDocker.failureMsg">
              <a-tag color="red">无法连接</a-tag>
            </a-tooltip>
          </template>

          <a-tooltip v-else title="集群关联的 docker 信息丢失,不能继续使用管理功能">
            <a-tag color="red">信息丢失</a-tag>
          </a-tooltip>
        </template>

        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <template v-if="record.machineDocker">
              <a-button
                size="small"
                :disabled="record.machineDocker.status !== 1"
                type="primary"
                @click="handleConsole(record, 'server')"
                >服务</a-button
              >
              <a-button
                size="small"
                :disabled="record.machineDocker.status !== 1"
                type="primary"
                @click="handleConsole(record, 'node')"
                >节点</a-button
              >
            </template>
            <template v-else>
              <a-button size="small" :disabled="true" type="primary">服务</a-button>
              <a-button size="small" :disabled="true" type="primary">节点</a-button>
            </template>

            <a-button size="small" type="primary" @click="handleEdit(record)">编辑</a-button>
            <a-button size="small" type="primary" danger @click="handleDelete(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
    <!-- 编辑集群区 -->
    <a-modal
      destroyOnClose
      :confirmLoading="confirmLoading"
      v-model:open="editVisible"
      title="编辑 Docker 集群"
      @ok="handleEditOk"
      :maskClosable="false"
    >
      <a-form ref="editForm" :rules="rules" :model="temp" :label-col="{ span: 4 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="集群名称" name="name">
          <a-input v-model:value="temp.name" placeholder="容器名称" />
        </a-form-item>

        <a-form-item label="标签" name="tag"
          ><a-input v-model:value="temp.tag" placeholder="关联容器标签" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 控制台 -->
    <!-- <a-drawer
      destroyOnClose
      :title="`${temp.name} 控制台`"
      placement="right"
      :width="`${this.getCollapsed ? 'calc(100vw - 80px)' : 'calc(100vw - 200px)'}`"
      :open="consoleVisible"
      @close="
        () => {
          this.consoleVisible = false
        }
      "
    > -->
    <console
      v-if="consoleVisible"
      :id="temp.id"
      :visible="consoleVisible"
      :initMenu="temp.menuKey"
      urlPrefix=""
      @close="
        () => {
          this.consoleVisible = false
        }
      "
    ></console>
    <!-- </a-drawer> -->
  </div>
</template>

<script>
import { CHANGE_PAGE, COMPUTED_PAGINATION, PAGE_DEFAULT_LIST_QUERY, parseTime } from '@/utils/const'
import { dockerSwarmList, editDockerSwarm, delSwarm } from '@/api/docker-swarm'
import { mapState } from 'pinia'
import Console from './console'
import { useGuideStore } from '@/stores/guide'
import { useUserStore } from '@/stores/user'
export default {
  components: {
    Console
  },
  props: {},
  data() {
    return {
      loading: true,
      listQuery: Object.assign({}, PAGE_DEFAULT_LIST_QUERY),
      list: [],
      temp: {},
      editVisible: false,
      consoleVisible: false,
      columns: [
        {
          title: '名称',
          dataIndex: 'name',
          ellipsis: true,
          tooltip: true
        },

        {
          title: '集群ID',
          dataIndex: 'swarmId',
          ellipsis: true,
          align: 'center',
          tooltip: true
        },
        {
          title: '容器标签',
          dataIndex: 'tag',
          ellipsis: true,
          tooltip: true
        },
        {
          title: '状态',
          dataIndex: ['machineDocker', 'status'],
          ellipsis: true,
          align: 'center',
          width: '100px'
        },
        {
          title: '最后修改人',
          dataIndex: 'modifyUser',
          width: 120,
          ellipsis: true
        },
        {
          title: '修改时间',
          dataIndex: 'modifyTimeMillis',
          sorter: true,
          ellipsis: true,
          customRender: ({ text }) => parseTime(text),
          width: '170px'
        },
        {
          title: '集群创建时间',
          dataIndex: ['machineDocker', 'swarmCreatedAt'],
          sorter: true,
          ellipsis: true,
          customRender: ({ text }) => parseTime(text),
          width: '170px'
        },
        {
          title: '集群修改时间',
          dataIndex: ['machineDocker', 'swarmUpdatedAt'],
          sorter: true,
          ellipsis: true,
          customRender: ({ text }) => parseTime(text),
          width: '170px'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          fixed: 'right',
          align: 'center',
          width: '220px'
        }
      ],
      rules: {
        // id: [{ required: true, message: "Please input ID", trigger: "blur" }],
        name: [{ required: true, message: '请填写集群名称', trigger: 'blur' }],

        tag: [
          { required: true, message: '请填写关联容器标签', trigger: 'blur' },
          { pattern: /^\w{1,10}$/, message: '标签限制为字母数字且长度 1-10' }
        ]
      },
      confirmLoading: false
    }
  },
  computed: {
    ...mapState(useUserStore, ['getUserInfo']),
    ...mapState(useGuideStore, ['getCollapsed']),
    pagination() {
      return COMPUTED_PAGINATION(this.listQuery)
    },
    useSuggestions() {
      if (this.loading) {
        // 加载中不提示
        return false
      }
      if (!this.getUserInfo || !this.getUserInfo.systemUser) {
        // 没有登录或者不是超级管理员
        return false
      }
      if (this.listQuery.page !== 1 || this.listQuery.total > 0) {
        // 不是第一页 或者总记录数大于 0
        return false
      }
      // 判断是否存在搜索条件
      const nowKeys = Object.keys(this.listQuery)
      const defaultKeys = Object.keys(PAGE_DEFAULT_LIST_QUERY)
      const dictOrigin = nowKeys.filter((item) => !defaultKeys.includes(item))
      return dictOrigin.length === 0
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    // 加载数据
    loadData(pointerEvent) {
      this.loading = true
      this.listQuery.page = pointerEvent?.altKey || pointerEvent?.ctrlKey ? 1 : this.listQuery.page

      dockerSwarmList(this.listQuery).then((res) => {
        if (res.code === 200) {
          this.list = res.data.result
          this.listQuery.total = res.data.total
        }
        this.loading = false
      })
    },
    // 修改
    handleEdit(record) {
      this.temp = record
      this.editVisible = true
      this.$refs['editForm']?.resetFields()
    },
    // 服务
    handleConsole(record, type) {
      this.temp = record
      this.temp = { ...this.temp, menuKey: type }
      this.consoleVisible = true
    },

    // 提交  数据
    handleEditOk() {
      // 检验表单
      this.$refs['editForm'].validate().then(() => {
        this.confirmLoading = true

        editDockerSwarm(this.temp)
          .then((res) => {
            if (res.code === 200) {
              // 成功
              $notification.success({
                message: res.msg
              })
              this.editVisible = false
              this.loadData()
            }
          })
          .finally(() => {
            this.confirmLoading = false
          })
      })
    },

    // 删除
    handleDelete(record) {
      const that = this
      this.$confirm({
        title: '系统提示',
        zIndex: 1009,
        content: '真的要删除该记录么？删除后构建关联的容器标签将无法使用',
        okText: '确认',
        cancelText: '取消',
        async onOk() {
          return await new Promise((resolve, reject) => {
            // 组装参数
            const params = {
              id: record.id
            }
            delSwarm(params)
              .then((res) => {
                if (res.code === 200) {
                  $notification.success({
                    message: res.msg
                  })
                  that.loadData()
                }
                resolve()
              })
              .catch(reject)
          })
        }
      })
    },

    // 分页、排序、筛选变化时触发
    changePage(pagination, filters, sorter) {
      this.listQuery = CHANGE_PAGE(this.listQuery, { pagination, sorter })
      this.loadData()
    }
  }
}
</script>

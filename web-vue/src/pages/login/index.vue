<template>
  <div class="wrapper" :style="backgroundImage">
    <svg
      width="100%"
      height="100%"
      viewBox="0 0 1440 500"
      stroke="none"
      stroke-width="1"
      fill="none"
      fill-rule="evenodd"
    >
      <g>
        <circle stroke="#13C2C2" cx="500" cy="-20" r="6"></circle>
        <circle fill-opacity="0.4" fill="#9EE6E6" cx="166" cy="76" r="8"></circle>
        <circle stroke="#13C2C2" cx="1165" cy="240" r="5"></circle>
        <circle stroke="#CED4D9" cx="1300" cy="10" r="8"></circle>
        <circle stroke="#ffffff" cx="1326.5" cy="180" r="6"></circle>
        <circle fill-opacity="0.4" fill="#9EE6E6" cx="944" cy="250" r="5"></circle>
      </g>
      <g>
        <path
          d="M1182.79367,448.230356 L1186.00213,453.787581 C1186.55442,454.744166 1186.22667,455.967347 1185.27008,456.519632 C1184.96604,456.695168 1184.62116,456.787581 1184.27008,456.787581 L1177.85315,456.787581 C1176.74858,456.787581 1175.85315,455.89215 1175.85315,454.787581 C1175.85315,454.436507 1175.94556,454.091619 1176.1211,453.787581 L1179.32957,448.230356 C1179.88185,447.273771 1181.10503,446.946021 1182.06162,447.498305 C1182.36566,447.673842 1182.61813,447.926318 1182.79367,448.230356 Z"
          stroke="#CED4D9"
        ></path>
        <path
          d="M1376.79367,204.230356 L1380.00213,209.787581 C1380.55442,210.744166 1380.22667,211.967347 1379.27008,212.519632 C1378.96604,212.695168 1378.62116,212.787581 1378.27008,212.787581 L1371.85315,212.787581 C1370.74858,212.787581 1369.85315,211.89215 1369.85315,210.787581 C1369.85315,210.436507 1369.94556,210.091619 1370.1211,209.787581 L1373.32957,204.230356 C1373.88185,203.273771 1375.10503,202.946021 1376.06162,203.498305 C1376.36566,203.673842 1376.61813,203.926318 1376.79367,204.230356 Z"
          stroke="#2F54EB"
        ></path>
      </g>
      <g>
        <rect stroke="#13C2C2" stroke-opacity="0.6" x="120" y="322" width="12" height="12" rx="1"></rect>
        <rect stroke="#CED4D9" x="108" y="1" width="9" height="9" rx="1"></rect>
      </g>
    </svg>
    <a-button class="switch" @click="handleToggleBg"
      ><LeftOutlined />&nbsp;{{ dynamicBg ? '关闭动态背景' : '开启动态背景' }}</a-button
    >
    <a-card class="login-card" hoverable>
      <a-card-meta :title="`${loginTitle}`" style="text-align: center" description="" />
      <br />
      <template v-if="action === 'login'">
        <a-form :model="loginForm" :label-col="{ span: 0 }" :wrapper-col="{ span: 24 }" @finish="handleLogin">
          <a-form-item name="loginName" :rules="[{ required: true, message: '请输入用户名' }]">
            <a-input autocomplete="true" v-model:value="loginForm.loginName" placeholder="用户名" />
          </a-form-item>
          <a-form-item name="userPwd" :rules="[{ required: true, message: '请输入密码' }]">
            <a-input-password autocomplete="true" v-model:value="loginForm.userPwd" placeholder="密码" />
          </a-form-item>
          <a-form-item v-if="!disabledCaptcha" name="code" :rules="[{ required: true, message: '请输入验证码' }]">
            <a-row>
              <a-col :span="14">
                <a-input v-model:value="loginForm.code" placeholder="验证码" />
              </a-col>
              <a-col :offset="2" :span="8">
                <div class="rand-code">
                  <img v-if="randCode" :src="randCode" @click="changeCode" />
                  <loading-outlined v-else />
                </div>
              </a-col>
            </a-row>
          </a-form-item>
          <a-form-item :wrapper-col="{ span: 24 }">
            <a-button type="primary" html-type="submit" class="btn-login" :loading="loading"> 登录 </a-button>
          </a-form-item>
          <template v-if="enabledOauth2Provides.length">
            <a-divider>第三方登录</a-divider>
            <a-form-item :wrapper-col="{ span: 24 }">
              <a-space :size="20">
                <div class="oauth2-item" v-if="enabledOauth2Provides.includes('gitee')">
                  <a-tooltip @click="toOauth2Url('gitee')" title="gitee">
                    <img alt="gitee" :src="giteeImg" />
                  </a-tooltip>
                </div>
                <div class="oauth2-item" v-if="enabledOauth2Provides.includes('maxkey')">
                  <a-tooltip @click="toOauth2Url('maxkey')" title="maxkey">
                    <img alt="maxkey" :src="maxkeyImg" />
                  </a-tooltip>
                </div>
                <div class="oauth2-item" v-if="enabledOauth2Provides.includes('github')">
                  <a-tooltip @click="toOauth2Url('github')" title="github">
                    <img alt="github" :src="githubImg" />
                  </a-tooltip>
                </div>
              </a-space>
            </a-form-item>
          </template>
        </a-form>
      </template>
      <template v-if="action === 'mfa'">
        <a-form
          ref="mfaDataForm"
          :label-col="{ span: 5 }"
          :wrapper-col="{ span: 19 }"
          :model="mfaData"
          @finish="handleMfa"
        >
          <a-form-item
            label="验证码"
            name="mfaCode"
            help="需要验证 MFA"
            :rules="[
              { required: true, message: '请输入两步验证码' },
              { pattern: /^\d{6}$/, message: '验证码 6 为纯数字' }
            ]"
          >
            <a-input v-model:value="mfaData.mfaCode" placeholder="mfa 验证码" />
          </a-form-item>

          <a-button type="primary" html-type="submit" class="btn-login"> 确认 </a-button>
        </a-form>
      </template>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { login, loginConfig, mfaVerify, oauth2Url, oauth2Login, loginRandCode } from '@/api/user/user'
import { checkSystem } from '@/api/install'
import sha1 from 'js-sha1'

import maxkeyImg from '@/assets/images/maxkey.png'
import giteeImg from '@/assets/images/gitee.svg'
import githubImg from '@/assets/images/github.png'
import { useGuideStore } from '@/stores/guide'

interface IFormState {
  loginName: string
  userPwd: string
  code: string
}
const guideStore = useGuideStore()

const theme = computed(() => {
  return guideStore.getThemeView()
})

const router = useRouter()
const route = useRoute()

const loginTitle = ref('登录JPOM')
const loginForm = reactive<IFormState>({
  loginName: '',
  userPwd: '',
  code: ''
})
const mfaData = reactive({
  mfaCode: '',
  token: ''
})
const loading = ref(false)
const action = ref<'mfa' | 'login'>('login')
const enabledOauth2Provides = ref<string[]>([])

const randCode = ref('')
const dynamicBg = ref(localStorage.getItem('dynamicBg') === 'true')
const disabledCaptcha = ref(false)

const backgroundImage = computed(() => {
  const color =
    theme.value === 'light' ? 'linear-gradient(#1890ff, #66a9c9)' : 'linear-gradient(rgb(38 46 55), rgb(27 33 36))'

  // background: linear-gradient(#1890ff, #66a9c9);
  return dynamicBg.value
    ? { backgroundImage: `url(https://picsum.photos/${screen.width}/${screen.height}/?random)`, background: color }
    : { background: color }
})

// 检查是否需要初始化
const beginCheckSystem = () => {
  checkSystem().then((res) => {
    if (res.code !== 200) {
      $notification.warn({
        message: res.msg
      })
    }
    if (res.code === 999) {
      router.push('/prohibit-access')
    } else if (res.code === 222) {
      router.push('/install')
    }
    if (res.data?.loginTitle) {
      loginTitle.value = res.data.loginTitle
    }

    checkOauth2()
  })
}

const getLoginConfig = () => {
  loginConfig().then((res) => {
    if (res.data && res.data.demo) {
      const demo = res.data.demo
      const p = h('p', { innerHTML: demo.msg }, [])
      $notification.info({
        message: '温馨提示',
        description: h('div', {}, [p])
      })
      loginForm.loginName = demo.user
    }
    disabledCaptcha.value = res.data.disabledCaptcha
    enabledOauth2Provides.value = res.data?.oauth2Provides || []

    changeCode()
  })
}
// change Code
const changeCode = () => {
  if (disabledCaptcha.value) {
    return
  }
  loginRandCode({ theme: theme.value, t: new Date().getTime() }).then((res) => {
    if (res.code === 200) {
      randCode.value = res.data
      loginForm.code = ''
    }
  })
}

const parseOauth2Provide = () => {
  if (jpomWindow.oauth2Provide === '<oauth2Provide>') {
    const pathname = location.pathname.substring(1)
    const pathArray = pathname.split('-')
    return pathArray[pathArray.length - 1]
    // console.log(location.pathname.substring(1))
  }
  return jpomWindow.oauth2Provide
}

const checkOauth2 = () => {
  if (route.query.code) {
    loading.value = true
    oauth2Login({
      code: route.query.code,
      state: route.query.state,
      provide: parseOauth2Provide()
    })
      .then((res) => {
        // 删除参数，避免刷新页面 code 已经被使用提示错误信息
        let query = Object.assign({}, route.query)
        delete query.code, delete query.state
        router.replace({
          query: query
        })
        // 登录不成功，更新验证码
        if (res.code !== 200) {
          changeCode()
        } else {
          startDispatchLogin(res)
        }
      })
      .finally(() => {
        loading.value = false
      })
  }
}
// 跳转到第三方系统
const toOauth2Url = (provide: string) => {
  oauth2Url({ provide: provide }).then((res) => {
    if (res.code === 200 && res.data) {
      $message.loading({ content: '跳转到第三方系统中', key: 'oauth2', duration: 0 })
      location.href = res.data.toUrl
    }
  })
}
const startDispatchLogin = (res: any) => {
  $notification.success({
    message: res.msg
  })
  const existWorkspace = res.data.bindWorkspaceModels.find((item: any) => item.id === appStore().getWorkspaceId())
  if (existWorkspace) {
    // 缓存的还存在
    dispatchLogin(res.data)
  } else {
    // 之前的工作空间已经不存在,切换到当前列表的第一个
    // 还没有选择工作空间，默认选中第一个 用户加载菜单
    let firstWorkspace = res.data.bindWorkspaceModels[0]
    appStore()
      .changeWorkspace(firstWorkspace.id)
      .then(() => {
        dispatchLogin(res.data)
      })
  }
}
const dispatchLogin = (data: any) => {
  // 调用 store action 存储当前登录的用户名和 token
  userStore()
    .login({ token: data.token, longTermToken: data.longTermToken })
    .then(() => {
      // 跳转主页面
      router.push({ path: '/' })
    })
}

// Controls the background display or hiding
const handleToggleBg = () => {
  dynamicBg.value = !dynamicBg.value
  localStorage.setItem('dynamicBg', String(dynamicBg.value))
}

const handleLogin = (values: IFormState) => {
  const params = {
    ...values,
    userPwd: sha1(loginForm.userPwd)
  }
  loading.value = true
  login(params)
    .then((res) => {
      if (res.code === 201) {
        action.value = 'mfa'
        mfaData.token = res.data.tempToken
        return
      }
      // 登录不成功，更新验证码
      if (res.code !== 200) {
        changeCode()
      } else {
        startDispatchLogin(res)
      }
    })
    .finally(() => {
      loading.value = false
    })
}

const handleMfa = () => {
  mfaVerify({
    token: mfaData.token,
    code: mfaData.mfaCode
  }).then((res) => {
    if (res.code === 201) {
      // 过期需要重新登录
      action.value = 'login'
      mfaData.token = ''
      mfaData.mfaCode = ''
    } else if (res.code === 200) {
      startDispatchLogin(res)
    }
  })
}

onMounted(() => {
  beginCheckSystem()

  getLoginConfig()
})

// export default {
//   data() {
//     return {
//       loginForm: {
//         loginName: '',
//         userPwd: '',
//         code: '',
//       },
//       mfaData: {},
//       action: 'login',
//       randCode: 'randCode.png',
//       dynamicBg: localStorage.getItem('dynamicBg') === 'true',
//       loginTitle: '登录JPOM',
//       rules: {
//         loginName: [{ required: true, message: '请输入用户名' }],
//         userPwd: [{ required: true, message: '请输入密码' }],
//         code: [{ required: true, message: '请输入验证码' }],
//         mfaCode: [
//           { required: true, message: '请输入两步验证码' },
//           { pattern: /^\d{6}$/, message: '验证码 6 为纯数字' },
//         ],
//       },
//       disabledCaptcha: false,
//       enabledOauth2Provides: [],
//       maxkeyImg: require(`@/assets/images/maxkey.png`),
//       giteeImg: require(`@/assets/images/gitee.svg`),
//       githubImg: require(`@/assets/images/github.png`),
//     }
//   },
//   created() {
//     this.checkSystem()
//     //this.getBg();

//     this.changeCode()
//     this.getLoginConfig()
//   },
//   computed: {
//     ...mapGetters(['getWorkspaceId']),
//     backgroundImage: function () {
//       if (this.dynamicBg) {
//         return {
//           backgroundImage: `url(https://picsum.photos/${screen.width}/${screen.height}/?random)`,
//         }
//       }
//       return {}
//     },
//   },
//   methods: {
//     // Get background pic
//     // getBg() {},
//     //
//   },
// }
</script>
<style scoped lang="less">
.wrapper {
  margin: 0;
  // width: 100vw;
  height: 100vh;
  /* background-color: #fbefdf; */

  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.switch {
  width: 128px;
  height: 38px;
  position: absolute;
  top: 49%;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  //background: #fff;
  // padding: 0 10px 0 10px;
  cursor: pointer;
  transform: translateX(105px);
  transition: all 0.3s ease-in-out;
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
}

.switch:hover {
  transform: translateX(0);
}

.login-card {
  min-width: 380px;
  border-radius: 8px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.rand-code {
  width: 100%;
  height: 32px;
}

.rand-code img {
  width: 100%;
  height: 100%;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  display: inherit;
}

.btn-login {
  width: 100%;
  margin: 10px 0;
}

:deep(.ant-card-meta-title) {
  font-size: 30px;
}

:deep(.ant-card-body) {
  padding: 30px;
}

.oauth2-item {
  width: 40px;
  height: 40px;
}

.oauth2-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>

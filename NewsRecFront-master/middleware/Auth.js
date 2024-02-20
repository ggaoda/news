import $axios from "@/API/AxiosHelper"
import {Log} from "@/utils";

export default async function ({store, route, redirect, error, params, req, app}) {
//用户路由拦截
  /**
   * 如果用户登陆了，则判断权限，如果是普通用户，则不可以进入后台
   */
  if (!store.state.isLogin) {
    //没有登陆
    //如果没有登录，则查看token是否存在，如果有，使用token进行登录
    const $cookie = require('js-cookie');
    console.log("11111111111111")
    //判断当前所处的位置，是前台还是后台
    let cookieName;
    let isAdmin = false
    if (route.fullPath.indexOf("admin") !== -1) {
      //后台
      cookieName = 'AdminAuthorization'
      isAdmin = true
    } else {
      cookieName = 'Authorization'
    }
    let token = $cookie.get(cookieName)
    console.log(cookieName)

    if (token) {
      console.log(token)

      console.log("22222222222222")
      let dataRes
      if (isAdmin) {
        console.log("33333333333333333")

        const {data} = await $axios.getAdmin("/v1/user/selfInfo")
        dataRes = data
      } else {
        console.log("888888888888888888")
        const {data} = await $axios.get("/v1/user/selfInfo")
        dataRes = data
      }
      if (dataRes.code === 200) {
        //登陆成功
        store.commit(`login`, dataRes.data)
        //已经登录，则进行权限检查
        permissionTest(store, route, redirect)
        return
      }
    }
    //登陆失败，清除token
    $cookie.remove(cookieName)
    //如果是在后台，则到指定页面进行登录
    if (isAdmin) {
      console.log("444444444444444")
      if (!route.fullPath.endsWith("/admin/login"))
        return redirect('/admin/login')
    }
  } else {
    //已经登录，则进行权限检查
    permissionTest(store, route, redirect)
  }


}

function permissionTest(store, route, redirect) {
  console.log("55555555555555555")
  console.log(store.state.user)

  if (store.state.user.role === "USER") {
    if (route.fullPath.indexOf("admin") !== -1 || (route.fullPath.endsWith('/login')) || route.fullPath.endsWith('/register')) {
      //如果普通用户访问的页面是后台的页面，则直接重定位到前台
      //或者如果进入了登录注册页面，也重定位到主页
      console.log("66666666666666666")

      return redirect('/')
    }
  } else {
    //管理员
    if ((route.fullPath.endsWith('/login'))) {
      console.log("77777777777777")

      //如果管理员已经登陆了，则直接定位到后台管理页面
      return redirect('/admin')
    }
  }
}

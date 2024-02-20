import axios from 'axios'
import qs from "qs";
import he from "element-ui/src/locale/lang/he";

let _ = require('lodash')
axios.defaults.timeout = 3000 // ms
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.withCredentials = true
axios.defaults.crossDomain = true

const $cookie = require('js-cookie');

export default {//导出方法
  getAdmin(url, params = {}, headers = {}) {
    let token = $cookie.get("AdminAuthorization")
    if (token) {
      token = "Bearer " + token
      headers["Authorization"] = token
    }
    return axios.get(url, {params: params, headers: headers})
  },
  postAdmin(url, params, headers) {
    let token = $cookie.get("AdminAuthorization")
    if (token) {
      token = "Bearer " + token
      headers["Authorization"] = token
    }
    if (headers['Content-Type'] === 'application/x-www-form-urlencoded') {
      return axios.post(url, qs.stringify(params), {headers: headers})
    } else {
      return axios.post(url, JSON.stringify(params), {headers: headers})
    }
  },
  get(url, params = {}, headers = {}) {
    let token = $cookie.get("Authorization")
    if (token) {
      token = "Bearer " + token
      headers["Authorization"] = token
    }
    return axios.get(url, {params: params, headers: headers})
  },
  post(url, params, headers) {
    let token = $cookie.get("Authorization")
    if (token) {
      token = "Bearer " + token
      headers["Authorization"] = token
    }
    if (headers['Content-Type'] === 'application/x-www-form-urlencoded') {
      return axios.post(url, qs.stringify(params), {headers: headers})
    } else {
      return axios.post(url, JSON.stringify(params), {headers: headers})
    }
  },
  put(url, params, headers = {}) {
    return axios.put(url, qs.stringify(params), {headers: headers})
  },
  patch(url, params, headers = {}) {
    return axios.patch(url, qs.stringify(params), {headers: headers})
  },
  delete(url, params, headers = {}) {
    return axios.delete(url, {params}, {headers: headers})
  },
}


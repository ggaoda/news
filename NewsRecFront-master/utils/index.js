// 解析请求头cookie的指定name值
export let $_ = require('lodash');
import CryptoJS from 'crypto-js'
import * as config from './config'

let key = '7410258963qwsedr';  //密钥必须为16或32位
let iv = '0123456789abcdef';

export function Log(...text) {
  if (config.model === "dev") {
    for (let i = 0; i < text.length; i++) {
      console.log(text[i]);
    }
  }
}

export function countTransform(count) {
  let fix = (decimal) => {
    return (count / decimal).toFixed(2);
  };
  if (count > 1000 && count < 10000) {
    return fix(1000) + 'k'
  } else if (count > 10000) {
    return fix(10000) + 'w'
  } else {
    return count
  }
}

function getAesString(data, key, iv) {//加密
  key = CryptoJS.enc.Utf8.parse(key);
  iv = CryptoJS.enc.Utf8.parse(iv);
  let encrypted = CryptoJS.AES.encrypt(data, key,
    {
      iv: iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
  return encrypted.toString();    //返回的是base64格式的密文
}

function getDAesString(encrypted, key, iv) {//解密
  key = CryptoJS.enc.Utf8.parse(key);
  iv = CryptoJS.enc.Utf8.parse(iv);
  let decrypted = CryptoJS.AES.decrypt(encrypted, key,
    {
      iv: iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
  return CryptoJS.enc.Utf8.stringify(decrypted);      //
}

export function encrypt(data) { //加密
  return getAesString(data, key, iv);   //密文
}

export function decode(data) {//解密
  return getDAesString(data, key, iv);//明文
}

export const parseCookieByName = (cookie, name) => {
  if (!cookie || !name) return '';
  let pattern = new RegExp(`(?:^|\\s)${name}=([^;]*)(?:;|$)`);
  let matched = cookie.match(pattern) || [];
  let value = matched[1] || '';
  return decodeURIComponent(value)
};


export function param2Obj(data) {

  return JSON.parse(
    '{"' +
    decodeURIComponent(data)
      .replace(/"/g, '\\"')
      .replace(/&/g, '","')
      .replace(/=/g, '":"') +
    '"}'
  )
}

export function transformTimeSample(time) {
  if ($_.isString(time)) {
    time = parseInt(time)
  }
  if (String(time).length === 10) {
    time *= 1000
  }

  let date = new Date(time);
  let Y = date.getFullYear() + '-';
  let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
  let D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
  return Y + M + D
}

export function transformTimeDetail(time) {
  if ($_.isString(time)) {
    time = parseInt(time)
  }
  time = time * 1000;
  let date = new Date(time);
  let Y = date.getFullYear() + '-';
  let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
  let D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + " ";

  let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
  let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
  let s = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());

  return Y + M + D + h + m + s
}

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime(time, cFormat = '{y}-{m}-{d} {h}:{i}:{s}') {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat;
  let date;
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string')) {
      if ((/^[0-9]+$/.test(time))) {
        // support "1548221490638"
        time = parseInt(time)
      } else {
        // support safari
        // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
        time = time.replace(new RegExp(/-/gm), '/')
      }
    }

    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  };
  const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    return value.toString().padStart(2, '0')
  });
  return time_str
}

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce(func, wait, immediate) {
  let timeout, args, context, timestamp, result;

  const later = function () {
    // 据上一次触发时间间隔
    const last = +new Date() - timestamp;

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null;
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args);
        if (!timeout) context = args = null
      }
    }
  };

  return function (...args) {
    context = this;
    timestamp = +new Date();
    const callNow = immediate && !timeout;
    // 如果延时不存在，重新设定延时
    if (!timeout) timeout = setTimeout(later, wait);
    if (callNow) {
      result = func.apply(context, args);
      context = args = null
    }
    return result
  }
}

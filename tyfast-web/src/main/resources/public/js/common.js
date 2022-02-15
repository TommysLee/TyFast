/**
 * 通用方法封装
 */

// RSA公钥
const _PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxAtMrDMwSg8YNFUTqKWV9W1GATkaLrHS/G2EExrx9uxdV6w8Rjab5Fv6QVKY6tsPdeAirSMK2foRp/KyszaT6ojlAmdRo95M5Dl6C7Rf7yoGbOa7kNoC6tY8172ojXDdjJJIbpq0YSkRfDOQkKhRnej5R42VCH3fURZKYUa3YsQIDAQAB";

// AJAX错误消息
const _AJAX_WARNING_MESSAGE = {
  0: {text: "网络连接异常，请稍后重试！", icon:"warning"},
  400: {text: "请求无效 (Bad request)", icon:"warning"},
  401: {text: "登录超时，请重新登录！", icon:"warning"},
  403: {text: "抱歉，您没有权限操作此资源！", icon:"warning"},
  404: {text: "请求地址不存在！", icon:"warning"},
  500: {text: "数据处理异常，请稍后重试！", icon:"error"},
  998: {text: "因浏览器限制，无法进行CROS跨域请求", icon:"warning"},
  999: {text: "未知错误", icon:"error"}
};

/**
 * 引入Toast插件
 */
Vue.prototype.$message = VuetifyMessageSnackbar.Notify;

/**
 * 引入表单验证插件VeeValidate
 */
Vue.component('ValidationProvider', VeeValidate.ValidationProvider);
Vue.component('ValidationObserver', VeeValidate.ValidationObserver);

/*
 * 扩展VeeValidate验证规则
 */
// 中文字符规则
VeeValidate.extend('chinese', {
  validate: value => {
    const reg = /^([\u4E00-\u9FA5\uF900-\uFA2D，。？！、；：【】“”‘’'']+)$/;
    return reg.test(value);
  },
  message: "{_field_}必须输入中文"
});

// 仅包含字母、数字、下划线、破折号等规则
VeeValidate.extend('letter_dash', {
  validate: value => {
    const reg = /^([A-Za-z0-9_/\-]+)$/;
    return reg.test(value);
  },
  message: "{_field_}只能包含字母、数字、下划线和破折号"
});

// Ajax异步验证的规则
VeeValidate.extend('async', {
  validate: async (value, {url, prop, ref}) => {
    // 请求参数
    let param = {};
    param[prop] = value;

    // 清除当前的错误状态
    if (app.$refs[ref + 'VP']) {
      app.$refs[ref + 'VP'].errors=[];
    }
    app.$refs[ref].loading = true;

    // 发送请求，并等待响应结果
    let result = {};
    await doAjax(ctx + url, param, (data) => {
      result = data;
    });
    app.$refs[ref].loading = false;
    return result.state? true : result.message;
  },
  params: ['url', 'prop', 'ref']
});

/**
 * 封装Axios Ajax
 */
function doAjax(url, params, callback, method, errCallback) {
  method = method || "POST";
  method = method.toUpperCase();
  params = params || {};
  errCallback = errCallback || function (){};

  // 全局自定义请求头
  axios.interceptors.request.use(config => {
    config.headers['X-Requested-With'] = 'XMLHttpRequest'; // Ajax请求标识
    return config;
  });

  // 发送请求
  if (url) {
    let _params = "";
    let promise;
    switch(method) {
      case "POST":
        for (let p in params) {
          let val = params[p];
          if (undefined != val && null != val) {
            _params += (p + "=" + encodeURIComponent(val) + "&")
          }
        }
        _params = _params.trim();
        params = _params.length > 0? _params.substr(0, _params.length - 1) : _params;
        promise = axios.post(url, params);
        break;
      default:
        params = {"params": params};
        promise = axios.get(url, params);
    }

    // Ajax回调
    promise.then((response) => {
      resetAjaxStatus();
      if (callback) {
        callback(response.data, response);
      }
    }).catch((err) => {
      resetAjaxStatus();
      console.log("Ajax请求异常：" + err);

      let status = 999;
      if (err.response) {
        status = err.response.status;
      } else if (err.request) {
        status = 0;
      }
      status = (status === 12029 || status === 12030)? 0 : status; // 兼容IE

      // 获取对应的错误消息对象
      let msgObj = _AJAX_WARNING_MESSAGE[status] || _AJAX_WARNING_MESSAGE[999];
      let msgText = msgObj.text;
      try {
        app.toast(msgText, msgObj.icon);
      } finally {
        // 登录超时，强制跳转到登录页
        if (401 === status) {
          setTimeout(function () {
            window.location.href = ctx + "/";
          }, 1500);
        } else {
          errCallback(msgText, err);
        }
      }
    });
    return promise;
  }
}

/**
 * 重置Ajax状态标记
 */
function resetAjaxStatus() {
  if (app) {
    if (app.loading) {
      app.loading = false;
    }
    if (app.posting) {
      app.posting = false;
    }
  }
}

/**
 * 扩展Date对象：新增时间格式化函数 format
 */
Object.defineProperty(Date.prototype, 'format', {
  enumerable: false,
  value: function(format) {
    let o={"M+":this.getMonth()+1,"d+":this.getDate(),"h+":this.getHours(),"m+":this.getMinutes(),"s+":this.getSeconds(),"q+":Math.floor((this.getMonth()+3)/3),"S":this.getMilliseconds()};
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (let k in o) if (new RegExp("(" + k + ")").test(format)) format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    return format;
  }
});

/**
 * 扩展Array对象：新增删除指定下标元素的函数 remove
 */
Object.defineProperty(Array.prototype, 'remove', {
  enumerable: false,
  value: function(index) {
    if ('number' == typeof(index) && index > -1 && index < this.length) {
      this.splice(index, 1);
    }
    return this;
  }
});

/**
 * 扩展Array对象：新增差集函数 diff
 */
Object.defineProperty(Array.prototype, 'diff', {
  enumerable: false,
  value: function(arr) {
    let diffArray = [];
    if (arr instanceof Array) {
      diffArray = arr.filter(currentValue => {
        return !this.includes(currentValue);
      });
    }
    return diffArray;
  }
});

/**
 * 扩展String对象：去除两边空格后，清除最后一个字符
 */
Object.defineProperty(String.prototype, 'clean', {
  enumerable: false,
  value: function(symbol) {
    let val = this.valueOf();
    let len = val.trim().length;
    if (len > 0) {
      symbol = symbol || ",";
      val = val.trim();
      if (val.endsWith(symbol)) {
        val = val.substr(0, len - 1);
      }
    }
    return val;
  }
});

/**
 * RSA公钥加密函数
 */
function rsaEncrypt(text) {
  if (null !== text && undefined !== text) {
    const crypt = new JSEncrypt();
    crypt.setPublicKey(_PUB_KEY); // 设置Base64公钥
    return crypt.encrypt(text);
  }
}

/**
 * 为数组元素添加 “序号” 属性
 */
function addIndexPropForArray(arr, pagination, indexName) {
  indexName = indexName || 'index';
  if (arr instanceof Array) {
    arr.map((item, index) => { // 添加 序号 字段
      let margin = 0;
      if (pagination) {
        margin = (pagination.page - 1) * pagination.pageSize;
      }
      item[indexName] = index + 1 + margin;
      return item;
    });
  }
  return arr;
}

/**
 * 时间比较函数
 */
function compareDate(date1, date2, operator) {
  let result = false;
  if (date1 instanceof Date && date2 instanceof Date) {
    let dms1 = date1.getTime();
    let dms2 = date2.getTime();
    switch (operator) {
      case "<":
        result = dms1 < dms2;
        break;
      case "<=":
        result = dms1 <= dms2;
        break;
      case ">":
        result = dms1 > dms2;
        break;
      case ">=":
        result = dms1 >= dms2;
        break;
      case "==":
        result = dms1 == dms2;
        break;
    }
  }
  return result;
}

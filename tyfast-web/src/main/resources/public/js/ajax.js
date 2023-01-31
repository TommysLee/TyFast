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
 * 将对象转换为查询字符串
 * 参数说明：https://github.com/ljharb/qs
 */
function queryStringify(obj, config) {
  if (obj) {
    config = Object.assign({allowDots: true, arrayFormat: 'comma', skipNulls:true}, config || {});
    return Qs.stringify(obj, config);
  }
  return obj;
}

/**
 * 封装Axios Ajax
 * 参数说明：https://axios-http.com/zh/docs/req_config
 */
function doAjax(url, params, callback, method, errCallback) {
  method = (method || "POST").toUpperCase();
  params = params || {};
  errCallback = errCallback || function (){};

  if (url) {
    // Ajax请求参数
    let options = {
      method,
      url,
      headers: {'X-Requested-With': 'XMLHttpRequest'}, // Ajax请求标识
      params
    };
    if ("GET" !== method) {
      options.params = null;
      options.data = params instanceof FormData? params : queryStringify(params);
    }

    // 发起请求
    return axios(options).then((response) => {
      resetAjaxStatus();
      if (callback) {
        callback(response.data, response);
      }
      return response;
    }).catch((err) => { // Ajax异常处理机制
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
      let msgText = t(msgObj.text);
      try {
        if (app && app.toast) {
          app.toast(msgText, msgObj.icon);
        } else {
          console.warn(msgText);
        }
      } finally {
        // 登录超时，强制跳转到登录页
        if (401 === status) {
          if (app && app.onSessionTimeout) {
            app.onSessionTimeout();
          } else {
            setTimeout(function () {
              window.location.href = ctx;
            }, 1500);
          }
        } else {
          errCallback(msgText, err);
        }
      }
    });
  }
}

/**
 * Ajax Get请求
 */
function doAjaxGet(url, params, callback, errCallback) {
  return doAjax(url, params, callback, "GET", errCallback);
}

/**
 * Ajax Post请求
 */
function doAjaxPost(url, params, callback, errCallback) {
  return doAjax(url, params, callback, null, errCallback)
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
    if (app.overlay) {
      app.overlay = false;
    }
  }
  window.confirmObj && window.confirmObj.finish();
}

/**
 * 批量Ajax请求，所有请求结束后，回调函数
 */
function doAjaxBatch(ajaxArray, callback) {
  Promise.allSettled(ajaxArray).then(callback);
}

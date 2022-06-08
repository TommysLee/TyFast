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
    await doAjaxPost(ctx + url, param, (data) => {
      result = data;
    });
    app.$refs[ref].loading = false;
    return result.state? true : result.message;
  },
  params: ['url', 'prop', 'ref']
});

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
    if ("GET" != method) {
      options.params = null;
      options.data = queryStringify(params);
    }

    // 发起请求
    return axios(options).then((response) => {
      resetAjaxStatus();
      if (callback) {
        callback(response.data, response);
      }
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

/**
 * 建立WebSocket STOMP连接
 */
let stompClient;
function connect() {
  let sock = new SockJS(ctx + "stomp");
  stompClient = webstomp.over(sock);
  stompClient.connect({}, function() {
    app.socketState = 9;

    // 连接成功后，订阅Topic：用于接收服务端消息
    console.log("STOMP服务器连接成功！请添加要订阅的Topic");
  }, function() {
    app.socketState = 1;
    console.log("STOMP服务器连接异常");

    // 5秒后自动重连
    setTimeout(() => {
      connect();
    }, 5000)
  });
  return stompClient;
}

/**
 * 计算辅助元素的高度
 */
function calcAssistHeight() {
  let totalHeight = 0;
  let assistArray = document.querySelectorAll(".assist") || [];
  for (let assist of assistArray) {
    totalHeight += assist.offsetHeight || 68;
  }

  let header = document.querySelector("header");
  if (header) {
    totalHeight += header.offsetHeight || 64;
  }
  return totalHeight;
}

/**
 * 查找节点的所有父节点
 */
function findParentsForTree(nodes, treeData, idKey) {
  let parents = [], nodeObjArray = [];
  if (treeData instanceof Array && treeData.length > 0) {
    // 递归查找 node 节点的父节点ID
    let findDownNode = function (val, node, path) {
      let flag = false;
      if (val.children) {
        for (let item of val.children) {
          if (node === item[idKey]) { // 找到节点
            flag = true;
            nodeObjArray.push(item);
            break;
          } else {
            flag = findDownNode(item, node, path); // 继续向下查找
            if (flag) {
              path.push(item); // 将父节点添加到结果数组
              break;
            }
          }
        }
      }
      return flag;
    };

    // 从根节点开始循环
    for (let node of nodes) {
      for (let item of treeData) {
        if (node === item[idKey]) { // 找到节点
          nodeObjArray.push(item);
          break;
        }
        let flag = findDownNode(item, node, parents); // 继续向下查找
        if (flag) {
          parents.push(item); // 将父节点添加到结果数组
        }
      }
    }
  }

  // 找到节点后，将 node 数组中的ID 替换为 节点对象
  if (nodeObjArray.length > 0) {
    nodes.splice(0, nodes.length);
    nodes.push(...nodeObjArray);
  }

  // 查找父节点时，是从下往上依次添加父节点，返回时，则反转处理，即从上往下的顺序
  // 过滤重复的父节点
  parents = parents.reverse().filter((el, index, self) => {
    return self.indexOf(el) === index;
  });
  return parents;
}

/**
 * 引入表单验证插件VeeValidate
 */
Vue.component('ValidationProvider', VeeValidate.ValidationProvider);
Vue.component('ValidationObserver', VeeValidate.ValidationObserver);

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
 * 四舍五入，最多保留2位小数
 * @param n
 */
function toRound(n) {
  if (typeof(n) === 'number') {
    n = Math.round(n * 100) / 100;
  }
  return n;
}

/**
 * 将数字转换为百分比形式，最多保留2位小数
 * @param n 数字
 */
function toPercent(n) {
  let p = n;
  if (typeof(n) === 'number') {
    p = Math.round(n * 100 * 100) / 100;
    p += '%';
  }
  return p;
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
      if (val.children && val.children.length > 0) {
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

/**
 * 距离下一个"分钟时刻"的时长（符合Cron规则）
 * @return 返回时长毫秒数
 */
function nextTimeTick(tick) {
  let gap = 0;
  if (tick) {
    let now = new Date();
    let minutes = now.getMinutes();
    gap = tick - minutes % tick;
    minutes += gap;

    // 下一时刻的具体时间点
    let nextTime = new Date();
    nextTime.setMinutes(minutes, 0, 0);

    // 时间间隔
    tick = tick * 60 * 1000; // 换算为毫秒数
    gap = Math.ceil(nextTime.getTime() - now.getTime());
    gap = gap >= tick * 0.2? gap : gap + tick;
  }
  return gap;
}

/**
 * 保存查询参数
 */
function saveQueryParam(menuName, value) {
  if (value && typeof(value) === 'object') {
    let valJson = null;
    try {
      if (Object.keys(value).length > 0) {
        let data = {name: menuName, value};
        valJson = JSON.stringify(data);
      }
    } finally {
      if (valJson) {
        sessionStorage.setItem("param", valJson);
      }
    }
  }
}

/**
 * 读取查询参数
 */
function readQueryParam(menuName, defaultValue) {
  let value = defaultValue || null;
  let valJson = sessionStorage.getItem("param");
  if (valJson) {
    try {
      let data = JSON.parse(valJson);
      if (menuName === data.name) {
        value = Object.assign(defaultValue || {}, data.value || {});
      } else {
        clearQueryParam();
      }
    } catch (e) {
      console.error(e);
    }
  }
  return value;
}

/**
 * 清除查询参数
 */
function clearQueryParam() {
  sessionStorage.removeItem("param");
}

/**
 * 将JSON字符串解析为JSON对象
 */
function parseJSON(jsonText, defaultVal) {
  if ('string' === typeof(jsonText)) {
    jsonText = jsonText.trim();
    if (jsonText.length > 0) {
      return JSON.parse(jsonText);
    }
  }
  return defaultVal || null;
}

/**
 * 数组转类Map结构
 */
function toMap(arr, itemValue, itemText, map) {
  map = map || {};
  itemValue = itemValue || 'value';
  itemText = itemText || 'text';
  if (arr instanceof Array) {
    for (let item of arr) {
      map[item[itemValue]] = item[itemText];
    }
  }
  return map;
}

/**
 * 载入语言包
 */
function loadLocaleMessages(locale, messages) {
  i18n && i18n.mergeLocaleMessage(locale, messages);
}

/**
 * 动态加载JavaScript文件
 */
function loadJScript(url, callback, errCallback) {
  if (url && typeof(url) === 'string') {
    let script = document.createElement("script");
    script.type = "text/javascript";
    script.src = url;
    script.onload = callback || function () {};
    script.onerror = errCallback || function () {};
    document.body.append(script);
  }
}

/**
 * Vue i18N t函数封装
 */
function t(data, p) {
  if ('undefined' !== typeof(i18n)) {
    if (data instanceof Array) {
      p = p || 'text';
      let pRaw = p+'Raw';
      for (let item of data) {
        item[pRaw] = item[pRaw] || item[p];
        item[p] = i18n.t(item[pRaw]);
      }
    } else {
      return i18n.t(data);
    }
  }
  return data;
}
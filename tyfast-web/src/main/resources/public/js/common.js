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
 * 当前时间的小时数是否与指定的值一致
 */
function isEqualHour(hour) {
  let flag = false;
  if (typeof(hour) === 'number') {
    let now = new Date();
    flag = hour === now.getHours();
  }
  return flag;
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

  let header = document.querySelector(".v-application__wrap > header");
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
        if (Object.keys(data).length > 2) {
          clearQueryParam();
        }
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
 * TTS语音播报
 */
function playTTS(text) {
  if (text) {
    if (SpeechSynthesisUtterance && speechSynthesis) {
      let utter = new SpeechSynthesisUtterance();
      utter.lang = 'zh';
      utter.text = text;
      speechSynthesis.speak(utter); // 加入到语音队列，TTS引擎会逐一播报
    } else {
      console.warn("浏览器不支持 Web Speech API.")
    }
  }
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
  itemText = itemText || 'title';
  if (arr instanceof Array) {
    for (let item of arr) {
      map[item[itemValue]] = item[itemText];
    }
  }
  return map;
}

/**
 * 数组列表转KV结构
 */
function toKV(arr, kname) {
  let map = {};
  if (arr instanceof Array) {
    for (let item of arr) {
      map[item[kname]] = item;
    }
  }
  return map;
}

/**
 * 载入语言包
 */
function loadLocaleMessages(locale, messages) {
  i18n && i18n.global.mergeLocaleMessage(locale, messages);
  if ('en-US' !== locale && vuetify && !vuetify.locale.messages.value[locale]) {
    vuetify.locale.messages.value[locale] = i18n.global.messages[locale]['$vuetify']
  }
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
      p = p || 'title';
      let pRaw = p+'Raw';
      for (let item of data) {
        item[pRaw] = item[pRaw] || item[p];
        item[p] = i18n.global.t(item[pRaw]);
      }
    } else {
      return i18n.global.t(data);
    }
  }
  return data;
}

/**
 * 获取URL查询参数
 */
function getQueryParam(name) {
  let v = null;
  let queryStr = window.location.search;
  if (queryStr) {
    let paramMap = Qs.parse(queryStr, { ignoreQueryPrefix: true });
    v = paramMap[name];
  }
  return v;
}

/**
 * 异步将图片文件转换为Base64
 */
function readImageAsBase64(file, target, prop) {
  if (file) {
    let fileReader = new FileReader();
    fileReader.onload = function(e) {
      (undefined !== target) && (target[prop||'url'] = e.target.result);
    }
    fileReader.readAsDataURL(file);
  }
}

/**
 * 生成UUSN
 */
function uusn(callback) {
  doAjaxGet(ctx + 'general/uusn', null, (result) => {
    callback && callback(result.data)
  })
}

/**
 * 后者数据是否较新
 */
function isYoungData(data1, data2) {
  let t1 = parseInt(data1.id || 0);
  let t2 = parseInt(data2.id || 0);
  return t2 > t1;
}

/**
 * 判断对象是否不为null 或 undefined
 */
function isNotBlank(obj) {
  return undefined !== obj && null != obj;
}

/**
 * 文本前缀相似度（比较：文本在目标文本的占比，即与目标文本前缀的连续相似度）
 *
 * @param text        文本
 * @param targetText  目标文本
 */
function prefixTextSimilarity(text, targetText) {
  let similarity = 0, count = 0;
  if (text && targetText) {
    text = text.replace(/\/\d+/g, '');
    targetText = targetText.replace(/\/\d+/g, '');
    const minLength = Math.min(text.length, targetText.length);
    for (let i = 0; i < minLength; i++) {
      if (text[i] === targetText[i]) {
        count++;
      } else {
        break;
      }
    }

    similarity = Math.round(count / targetText.length * 100) / 100;
    similarity = similarity < 1? similarity : 1;
  }
  return {similarity, count}
}
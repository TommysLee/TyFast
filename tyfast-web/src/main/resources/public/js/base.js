// RSA公钥
const _PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxAtMrDMwSg8YNFUTqKWV9W1GATkaLrHS/G2EExrx9uxdV6w8Rjab5Fv6QVKY6tsPdeAirSMK2foRp/KyszaT6ojlAmdRo95M5Dl6C7Rf7yoGbOa7kNoC6tY8172ojXDdjJJIbpq0YSkRfDOQkKhRnej5R42VCH3fURZKYUa3YsQIDAQAB";

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
 * 扩展Date对象：新增时间格式化函数 format
 */
Object.defineProperty(Date.prototype, 'format', {
  enumerable: false,
  value: function(format) {
    const opt = {
      'y+': this.getFullYear().toString(), // 年
      'M+': (this.getMonth() + 1).toString(), // 月
      'd+': this.getDate().toString(), // 日
      'h+': this.getHours().toString(), // 时
      'm+': this.getMinutes().toString(), // 分
      's+': this.getSeconds().toString() // 秒
    };

    for (const k in opt) {
      const ret = new RegExp('(' + k + ')').exec(format);
      if (ret) {
        if (/(y+)/.test(k)) {
          format = format.replace(ret[1], opt[k].substring(4 - ret[1].length));
        } else {
          format = format.replace(ret[1], (ret[1].length === 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, '0')));
        }
      }
    }
    return format;
  }
});

/**
 * 扩展Date对象：新增静态函数 of，将日期字符串转换为日期对象
 */
Date.of = function(dateText) {
  let date = new Date(0);
  if (typeof(dateText) === 'string') {
    dateText = dateText.trim();
    date = new Date(dateText);
    if (!date.getTime()) {
      date = new Date(dateText.replace(/-/g, "/"));
      if (!date.getTime()) {
        date = new Date(dateText.replace(/\s+/g, "T"));
      }
    }
  }
  return date;
};

/**
 * 扩展Number对象：新增格式化函数 format
 */
Object.defineProperty(Number.prototype, 'format', {
  enumerable: false,
  value: function(locale, options) {
    if (!(typeof(locale) === 'string') && !options) {
      options = locale;
      locale = null;
    }
    return Intl.NumberFormat(locale || navigator.language || 'zh-CN', options ?? {}).format(this);
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

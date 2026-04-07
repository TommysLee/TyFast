/**
 * 单位换算类
 */
const Units = {
  SUPPORTED_UNITS: {
    energy: { // 能源单位
      W: {
        units: ['W', 'kW', 'MW', 'GW', 'TW', 'PW'],
        ratio: 1000
      },
      kW: {
        units: ['kW', 'MW', 'GW', 'TW', 'PW'],
        ratio: 1000
      },
      Wh: {
        units: ['Wh', 'kWh', 'MWh', 'GWh', 'TWh'],
        ratio: 1000
      },
      kWh: {
        units: ['kWh', 'MWh', 'GWh', 'TWh'],
        ratio: 1000
      }
    },
    len: { // 长度单位
      m: {
        units: ['m', 'km'],
        ratio: 1000
      }
    },
    time: { // 时间单位
      '秒': {
        units: ['秒', '分钟', '小时'],
        ratio: 60
      }
    }
  },

  /**
   * 自动单位换算
   *
   * @param val 当前值
   * @param unit 当前值的计量单位
   * @param type 单位类别
   * @param strict 严格模式
   */
  autoUnitConversion(val, unit, type, strict = false) {
    let measure = Units.SUPPORTED_UNITS[type][unit];
    if (!measure) {
      console.warn('当前值的单位：', unit, ' 在 ', type, ' 范围内不受支持')
      return Units.result(unit, val, val)
    }
    if (!strict) {
      if (Math.abs(val) < measure.ratio * 10) {
        return Units.result(measure.units[0], val, val)
      }
    }

    let symbol = 1;
    if (val < 0) {
      symbol = -1;
      val *= symbol;
    }

    let i = Math.floor(Math.log(val)/Math.log(measure.ratio));
    i = i >= measure.units.length? (measure.units.length - 1) : i < 0? 0 : i;
    return Units.result(measure.units[i], Math.round(val * symbol / Math.pow(measure.ratio, i)), val)
  },

  /**
   * 结果集数据结构
   *
   * @param unit  单位
   * @param val   转换后的值
   * @param raw   原始值
   */
  result(unit, val, raw) {
    return {unit, val, raw, text: val + unit}
  },

  /**
   * 单位自动换算包装类
   */
  wrapE(val, unit, type, strict) {
    if (typeof(val) === 'number') {
      return Units.autoUnitConversion(val, unit, type, strict);
    } else {
      console.warn(val, '不是一个有效数值', typeof(val))
    }
    return null;
  },

  /**
   * 长度单位自动换算
   *
   * @param val   当前值
   * @param unit  当前值的计量单位
   * @param strict 严格模式
   */
  lenAutoUnitConversion(val, unit, strict) {
    return Units.wrapE(val, unit, 'len', strict);
  },
  luc(val, unit, strict) {
    unit = unit || 'm';
    return Units.lenAutoUnitConversion(val, unit, strict)
  },

  /**
   * 时间单位自动换算
   *
   * @param val   当前值
   * @param unit  当前值的计量单位
   * @param strict 严格模式
   */
  timeAutoUnitConversion(val, unit, strict) {
    return Units.wrapE(val, unit, 'time', strict);
  },
  tuc(val, unit, strict) {
    unit = unit || '秒';
    return Units.timeAutoUnitConversion(val, unit, strict)
  },

  /**
   * 能源单位自动换算
   *
   * @param val   当前值
   * @param unit  当前值的计量单位
   * @param strict 严格模式
   */
  energyAutoUnitConversion(val, unit, strict) {
    return Units.wrapE(val, unit, 'energy', strict);
  },
  euc(val, unit, strict) {
    unit = unit || 'kW';
    return Units.energyAutoUnitConversion(val, unit, strict)
  }
}
/*
 * Baidu ECharts 异形图表辅助类
 * @Author TyCode
 */

function EChartsExtension() {}

/**
 * 注册Resize事件，实现图表自适应
 *
 * @param instance ECharts实例
 */
EChartsExtension.registerResizeEvent = function (instance) {
    window.addEventListener("resize", () => {
        instance.resize();
    })
};

/**
 * 显示Loading提示
 *
 * @param instance ECharts实例
 */
EChartsExtension.showLoading = function (instance, text, showSpinner) {
    showSpinner = typeof(showSpinner) === 'boolean'? showSpinner : true;
    instance && instance.showLoading({text: text || '正在加载中...', showSpinner, maskColor: 'rgba(255, 255, 255, 0.3)'});
};

/**
 * 隐藏Loading提示
 *
 * @param instance ECharts实例
 */
EChartsExtension.hideLoading = function (instance) {
    instance && instance.hideLoading();
};

/**
 * 初始化ECharts实例
 *
 * @param dom Dom对象
 * @param chartOptions ECharts参数
 * @param theme 主题
 */
EChartsExtension.init = function (dom, chartOptions, theme) {
    let instance = echarts.init(dom, theme);
    EChartsExtension.registerResizeEvent(instance);
    instance.setOption(chartOptions);
    return instance;
};

/**
 * 仪表盘
 */
EChartsExtension.gauge = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = echarts.util.merge({
            title: '',          // 图表名称
            titleStyle: {},     // 样式
            min: 0,             // 最小值
            max: 100,           // 最大值
            prefix: '',         // 数值前缀
            suffix: '',         // 数值后缀
            fontSize: 40,       // 字体大小
            splitNumber: null,  // 表盘分割数量
            theme: null,        // 主题
            color: null,        // 仪表盘颜色
            backgroundColor: null,  // 背景色
            showTooltip: false, // 是否显示Tooltip
            showProgress: true  // 是否显示Progress
        }, options || {}, true);

        const chartOptions = {
            series: [{
                type: 'gauge',
                name: options.title,
                min: options.min,
                max: options.max,
                axisTick: {show: true},
                detail: {
                    valueAnimation: true,
                    offsetCenter: [0, '55%'],
                    fontSize: options.fontSize,
                    formatter: options.prefix + '{value}' + options.suffix
                },
                itemStyle: {
                    color: options.color
                },
                title: options.titleStyle
            }]
        };
        if (options.splitNumber) {
            chartOptions.series[0].splitNumber = options.splitNumber;
        }
        if (options.backgroundColor) {
            chartOptions.backgroundColor = options.backgroundColor;
        }
        if (options.showTooltip) {
            chartOptions.tooltip = {formatter: '{a} <br/>{b} : {c}%'};
        }
        if (options.showProgress) {
            chartOptions.series[0].progress = {show: true};
        }
        if (data) {
            chartOptions.series[0].data = data;
        }

        return EChartsExtension.init(dom, chartOptions, options.theme);
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        instance.setOption({
            series: [{data}]
        });
    }
};

/**
 * 气压表
 */
EChartsExtension.barometer = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = echarts.util.merge({
            theme: null,            // 主题
            outerMin: 0,            // 外层最小值
            outerMax: 100,          // 外层最大值
            outerSplitNumber: 10,   // 外层分割数量
            innerMin: 0,            // 内层最小值
            innerMax: 60,           // 内层最大值
            innerSplitNumber: 6     // 内层分割数量
        }, options || {}, true);
        let color = options.theme? '#fff' : '#000'

        const chartOptions = {
            series: [
                {
                    type: 'gauge',
                    min: options.outerMin,
                    max: options.outerMax,
                    splitNumber: options.outerSplitNumber,
                    radius: '80%',
                    axisLine: {
                        lineStyle: {color: [[1, '#f00']], width: 3}
                    },
                    splitLine: {distance: -18, length: 18, lineStyle: {color: '#f00'}},
                    axisTick: {distance: -12, length: 10, lineStyle: {color: '#f00'}},
                    axisLabel: {distance: -50, color: '#f00', fontSize: 25},
                    anchor: {show: true, size: 20, itemStyle: {borderColor: color, borderWidth: 2}},
                    pointer: {
                        offsetCenter: [0, '10%'],
                        icon: 'path://M2090.36389,615.30999 L2090.36389,615.30999 C2091.48372,615.30999 2092.40383,616.194028 2092.44859,617.312956 L2096.90698,728.755929 C2097.05155,732.369577 2094.2393,735.416212 2090.62566,735.56078 C2090.53845,735.564269 2090.45117,735.566014 2090.36389,735.566014 L2090.36389,735.566014 C2086.74736,735.566014 2083.81557,732.63423 2083.81557,729.017692 C2083.81557,728.930412 2083.81732,728.84314 2083.82081,728.755929 L2088.2792,617.312956 C2088.32396,616.194028 2089.24407,615.30999 2090.36389,615.30999 Z',
                        length: '115%',
                        itemStyle: {color}
                    },
                    detail: {valueAnimation: true, precision: 1},
                    title: {offsetCenter: [0, '-50%']}
                },
                {
                    type: 'gauge',
                    min: options.innerMin,
                    max: options.innerMax,
                    splitNumber: options.innerSplitNumber,
                    axisLine: {
                        lineStyle: {color: [[1, color]], width: 3}
                    },
                    splitLine: {distance: -3, length: 18, lineStyle: {color}},
                    axisTick: {distance: 0, length: 10, lineStyle: {color}},
                    axisLabel: {distance: 10, fontSize: 25, color},
                    pointer: {show: false},
                    title: {show: false},
                    anchor: {show: true, size: 14, itemStyle: {color}}
                }
            ]
        };
        if (data) {
            chartOptions.series[0].data = data;
        }

        return EChartsExtension.init(dom, chartOptions, options.theme);
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        instance.setOption({
            series: [{data}]
        });
    }
};

/**
 * 气温仪表盘
 */
EChartsExtension.temperature = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = echarts.util.merge({
            min: -20,               // 最小值
            max: 100,               // 最大值
            splitNumber: 12,        // 分割数量
            prefix: '',             // 数值前缀
            suffix: '',             // 数值后缀
            fontSize: 60,           // 字体大小
            fontWeight: 'bolder',   // 字体样式
            theme: null,            // 主题
            showProgress: true  // 是否显示Progress
        }, options || {}, true);

        const chartOptions = {
            series: [
                {
                    type: 'gauge',
                    center: ['50%', '60%'],
                    startAngle: 200,
                    endAngle: -20,
                    min: options.min,
                    max: options.max,
                    splitNumber: options.splitNumber,
                    itemStyle: {color: '#FFAB91'},
                    pointer: {show: false},
                    axisLine: {lineStyle: {width: 30}},
                    axisTick: {distance: -45, splitNumber: 5, lineStyle: {width: 2, color: '#999'}},
                    splitLine: {distance: -52, length: 14, lineStyle: {width: 3, color: '#999'}},
                    axisLabel: {distance: -20, color: '#999', fontSize: 20},
                    anchor: {show: false},
                    title: {show: false},
                    detail: {
                        valueAnimation: true,
                        width: '60%',
                        lineHeight: 40,
                        borderRadius: 8,
                        offsetCenter: [0, '-15%'],
                        fontSize: options.fontSize,
                        fontWeight: options.fontWeight,
                        color: 'auto',
                        formatter: options.prefix + '{value}' + options.suffix
                    }
                },
                {
                    type: 'gauge',
                    center: ['50%', '60%'],
                    startAngle: 200,
                    endAngle: -20,
                    min: options.min,
                    max: options.max,
                    itemStyle: {color: '#FD7347'},
                    pointer: {show: false},
                    axisLine: {show: false},
                    axisTick: {show: false},
                    splitLine: {show: false},
                    axisLabel: {show: false},
                    detail: {show: false}
                }
            ]
        };
        if (options.showProgress) {
            chartOptions.series[0].progress = {show: true, width: 30};
            chartOptions.series[1].progress = {show: true, width: 8};
        }
        if (data) {
            chartOptions.series[0].data = data;
            chartOptions.series[1].data = data;
        }

        return EChartsExtension.init(dom, chartOptions, options.theme);
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        instance.setOption({
            series: [{data}, {data}]
        });
    }
};

/**
 * 雷达图
 */
EChartsExtension.radar = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = echarts.util.merge({
            title: null,        // 图表名称
            prefix: '',         // 数值前缀
            suffix: '',         // 数值后缀
            legend: null,       // 图例数据
            dimensions: null,   // 维度
            theme: null,        // 主题
            showTooltip: false, // 是否显示Tooltip
            showArea: false,    // 是否突出显示
            focus: true         // 是否聚焦显示
        }, options || {}, true);

        const chartOptions = {
            radar: {indicator: options.dimensions || []},
            series: [{
                type: 'radar',
                emphasis: {
                    disabled: false
                },
                symbol: 'none',
                label: {
                    show: true,
                    formatter: function (params) {
                        return options.prefix + params.value + options.suffix;
                    }
                }
            }],
        };
        if (options.title) {
            chartOptions.title = {text: options.title};
        }
        if (options.legend) {
            chartOptions.legend = {data: options.legend};
        }
        if (options.showTooltip) {
            chartOptions.tooltip = {trigger: 'item', show: true};
        }
        if (options.showArea) {
            chartOptions.series[0].emphasis.areaStyle = {color: '#4AF94B', opacity: 0.3};
        }
        if (options.focus) {
            chartOptions.series[0].emphasis.lineStyle = {width: 4};
        }
        if (data) {
            chartOptions.series[0].data = data;
        }

        return EChartsExtension.init(dom, chartOptions, options.theme);
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        instance.setOption({
            series: [{data}]
        });
    }
};

/**
 * 桑基图
 */
EChartsExtension.sankey = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = echarts.util.merge({
            title: null,        // 图表名称
            theme: null,        // 主题
            unit: '',           // 计量单位
            showTooltip: false, // 是否显示Tooltip
            focus: true         // 是否聚焦显示
        }, options || {}, true);

        const chartOptions = {
            series: [{
                type: 'sankey',
                layout: 'none',
                lineStyle: {color: 'gradient', curveness: 0.5}
            }],
            label: {
                formatter: (params) => {
                    let label = params.name
                    let value = params.data.nvalue ?? -1;
                    if (value >= 0) {
                        value = Intl.NumberFormat().format(value);
                        label += ' ' + value + options.unit
                    }
                    return label;
                }
            }
        };
        if (options.title) {
            chartOptions.title = {text: options.title};
        }
        if (options.showTooltip) {
            chartOptions.tooltip = {trigger: 'item', triggerOn: 'mousemove'};
        }
        if (options.focus) {
            chartOptions.series[0].emphasis = {focus: 'adjacency'};
        }
        if (data) {
            let sankeyData = EChartsExtension.sankey.format(data);
            chartOptions.series[0].data = sankeyData[0];
            chartOptions.series[0].links = sankeyData[1];
        }

        return EChartsExtension.init(dom, chartOptions, options.theme);
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        let sankeyData = EChartsExtension.sankey.format(data);
        instance.setOption({
            series: [{
                data: sankeyData[0],
                links: sankeyData[1]
            }]
        });
    },

    /**
     * 格式化数据
     */
    format: (data) => {
        let sankeyData = [[],data];
        if (data) {
            let sources = [];
            let snames = {};
            let targets = [];

            for (let v of data) {
                if (!snames[v.source]) {
                    sources.push({name: v.source, nvalue: v.svalue});
                    snames[v.source] = true;
                }
                targets.push({name: v.target, nvalue: v.value});
            }

            // 去除targets中与sources重复的元素
            targets = targets.filter(item => !snames[item.name]);

            // 合并targets中的重复元素
            let groups = targets.reduce((groups, item) => {
                const {name} = item;
                groups[name] = groups[name] ?? [];
                groups[name].push(item);
                return groups;
            }, {});
            targets = [];
            for (let p in groups) {
                let nvalue = 0;
                for (let item of groups[p]) {
                    nvalue += item.nvalue;
                }
                targets.push({name: p, nvalue})
            }

            // 合并sources与targets，并赋值
            sankeyData[0] = sources.concat(targets);
        }
        return sankeyData;
    }
};

/**
 * 水波图
 */
EChartsExtension.wave = {
    /**
     * 构建图表
     *
     * @param dom Dom对象 或 Dom ID
     * @param options 构建参数
     * @param data 数据
     */
    build: (dom, options, data) => {
        dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
        options = options || {};
        let color = ['#294D99', '#156ACF', '#1598ED', '#45BDFF'];
        if (typeof(options.color) === 'string') {
            color[0] = options.color;
        } else if (options.color instanceof Array) {
            for (let i = 0; i < options.color.length; i++) {
                color[i] = options.color[i];
            }
        }
        options.color = color;

        options = echarts.util.merge({
            title: '',          // 图表名称
            color: null,        // 颜色列表
            fontColor: null,    // 字体颜色
            fontSize: null,     // 字体大小
            prefix: '',         // 数值前缀
            suffix: '%',        // 数值后缀
            shape: 'circle',    // 形状，可选值：'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow', 'container'
            amount: 3,          // 水波数量
            theme: null,        // 主题
            silent: false,      // 静默模式
            transparent: false, // 背景是否透明
            showValue: true,    // 是否显示数值
            showBorder: false   // 是否显示边框
        }, options, true);

        const chartOptions = {
            series: [{
                type: 'liquidFill',
                name: options.title,
                shape: options.shape,
                color: options.color,
                outline: {
                    show: options.showBorder,
                    borderDistance: 0,
                    itemStyle: {
                        borderWidth: 5,
                        borderColor: color[0]
                    }
                },
                label: {
                    formatter: function(param) {
                        return (param.seriesName? (param.seriesName + '\n') : '') + (options.showValue? (options.prefix + (Math.round(param.value * 10000)/100) + options.suffix) : '');
                    }
                },
                data: EChartsExtension.wave.format(data, options.amount)
            }],
            silent: options.silent
        };
        if (options.title) {
            chartOptions.series[0].name = options.title;
        }
        if (options.transparent) {
            chartOptions.series[0].backgroundStyle = {color: 'transparent'};
        }
        if (options.fontColor) {
            chartOptions.series[0].label.color = options.fontColor;
        }
        if (options.fontSize) {
            chartOptions.series[0].label.fontSize = options.fontSize;
        }

        let instance = EChartsExtension.init(dom, chartOptions, options.theme);
        instance.amount = options.amount;
        return instance;
    },

    /**
     * 更新图表数据
     *
     * @param instance ECharts实例
     * @param data 数据
     */
    update: (instance, data) => {
        instance.setOption({
            series: [{
                data: EChartsExtension.wave.format(data, instance.amount || 1)
            }]
        });
    },

    /**
     * 格式化数据
     */
    format: (data, amount) => {
        if (typeof(data) === 'number') {
            let dataArray = [data];
            let val = data;
            while (amount > 1 && val > 0.01) {
                val = Math.round(val * 0.6 * 100) / 100;
                if (val < 0.01) {
                    break;
                }
                dataArray.push(val);
                amount--;
            }
            return dataArray;
        }
        return [0];
    }
};

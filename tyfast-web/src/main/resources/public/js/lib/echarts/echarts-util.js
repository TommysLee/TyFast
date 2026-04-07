function EchartsUtil() {}
EchartsUtil.lineDistance = [32, 5];

/**
 * 构建ECharts图表参数（支持折线图、柱状图、饼图）
 *
 * @param chartType 图表类型
 * @param dataset 数据集
 * @param startOptions 常用功能一键启动参数
 * @param standardOptions 官方标准化参数（标准化参数优先级高于一键启动参数）
 */
EchartsUtil.build = function(chartType, dataset, startOptions, standardOptions) {
    let options = {}, boundaryGap = false, dlen = 0;
    startOptions = startOptions || {};
    standardOptions = standardOptions || {};

    /*
     * 支持一键启动的功能
     */
    startOptions = Object.assign({}, {
        showLegend: false,    // 是否显示图例，默认：false
        showSymbol: false,    // 是否显示 symbol
        showArea: false,      // 是否显示 区域填充样式
        showAxisUnit: true,   // Y轴是否显示Unit
        showMaxPoint: true,   // 是否显示最大值Point，默认：true
        showMinPoint: true,   // 是否显示最小值Point，默认：true
        showAvgLine: true,    // 是否显示均值线，默认：true
        showTooltip: true,    // 是否显示Tooltip，默认：true
        showToolbox: true,    // 是否显示工具栏，默认：true
        showMagicType: false, // 是否显示动态类型切换
        showLabel: false,     // 是否显示值文本
        showZoomBar: false,   // 是否显示 X 轴 DataZoom Bar，默认：false
        enableZoom: true,     // 是否启用 X 轴 区域放大功能，默认：true
        enableGradient: true, // 是否启用图表渐变色（仅对折线图有效），默认：true
        enableMyRestoreTool: true, // 启用自定义的Restore工具，以替换原生的Restore工具
        smooth: false,      // 平滑曲线
        step: false,        // 阶梯曲线
        inverse: false,     // 图表反转
        symbolSize: 50,     // 标记大小
        title: null,        // 图表标题
        unit: null,         // 系列值的显示单位
        axisName: null,     // Y轴标题
        labelPosition: 'right',  // 值文本位置
        lines: [],          // Y轴自定义划线，每个Line的格式说明：{pos: Y轴坐标值, text: 显示的文本, color: 线颜色}
        ymin: null,         // Y轴最小值
        ymax: null          // Y轴最大值
    }, startOptions);

    /*
     * 数据集
     */
    if (dataset) {
        options.dataset = dataset;
        dlen = dataset.dimensions.length;
    }

    /*
     * 将一键启动参数翻译为ECharts标准化参数
     */
    // 图例
    if (startOptions.showLegend) {
        let legend = {left: 'left'};
        if ("pie" === chartType) {
            legend.orient = 'vertical';
        }
        options.legend = echarts.util.merge(legend, standardOptions.legend||{}, true);
    }

    // 系列 和 最大值Point & 最小值Point & 均值线 & 自定义线
    let series = {type: chartType};
    if (startOptions.showLabel) {
        series.label = {show: true, position: startOptions.labelPosition};
    }
    switch(chartType) {
        case "line":
            series.showSymbol = startOptions.showSymbol;
            series.smooth = startOptions.smooth;
            series.step = startOptions.step? 'middle' : null;
            series.areaStyle = startOptions.showArea? {} : null;
            series.markLine = {silent: true};
        case "bar":
            // 最大值Point & 最小值Point
            let pointData = [];
            if (startOptions.showMaxPoint) {
                pointData.push({ type: 'max', name: 'Max', label:{color:"#fff"}, symbolSize: startOptions.symbolSize });
            }
            if (startOptions.showMinPoint) {
                pointData.push({ type: 'min', name: 'Min', label:{color:"#fff"}, symbolSize: startOptions.symbolSize });
            }
            series.markPoint = {data: pointData};

            // 均值线 & 自定义线
            let lineData = [];
            if (startOptions.showAvgLine) {
                let lineStyle = dlen > 2? null : {color: '#F72C5B'};
                lineData.push(...[{type: 'average'}, {type: 'average', lineStyle, label: {formatter: '平均值', position: 'start', distance: EchartsUtil.lineDistance}}]);
            }

            let axisPropName = startOptions.inverse? "xAxis" : "yAxis";
            for (let line of startOptions.lines) {
                let d = {};
                d[axisPropName] = line.pos;
                if (line.text) {
                    d.label = {formatter: line.text, color: line.textColor, position: line.position || 'start', distance: line.distance || EchartsUtil.lineDistance};
                }
                if (line.color) {
                    d.lineStyle = {color: line.color};
                }
                let lo = {};
                lo[axisPropName] = axisPropName;
                lineData.push(...[lo, d]);
            }
            series.markLine = series.markLine || {};
            series.markLine.data = lineData;

            // 显示背景
            series.showBackground = true;
            break;
        case "pie":
            series.label = {formatter: "{b}: {@value}" + (startOptions.unit || "") + " ({d}%)"};
            break;
    }
    options.series = [];
    startOptions.series = startOptions.series || [];
    for (let i = 1; i < dlen; i++) {
        let _series = {...series};
        let dim = dataset.dimensions[i];
        if (dim.style) { // 若Dataset中指定了图表类型，则优先使用，否则，使用默认图表类型
            _series.type = dim.style;
            boundaryGap = "line" !== dim.style? true : boundaryGap;
        }

        if (startOptions.itemColors instanceof Array && startOptions.itemColors.length > 0) {
            _series.itemStyle = _series.itemStyle || {};
            _series.itemStyle.color = function(param) {
                let color = param.color;
                let val = param.value;
                if (val.hasOwnProperty("cindex")) {
                    color = startOptions.itemColors[val.cindex] || color;
                }
                return color;
            };
        }
        options.series.push(echarts.util.merge(_series, startOptions.series[i-1] || {}, true));
    }

    // Tooltip
    if (startOptions.showTooltip) {
        let tooltip = {
            className: 'chart-tooltip'
        };
        if (startOptions.unit) {
            tooltip.valueFormatter = value => (value || value == 0 ? value : "") + startOptions.unit;
        }
        if ("pie" === chartType) {
            tooltip.trigger = "item";
        } else {
            if ("bar" === chartType) {
                tooltip.axisPointer = {type: 'shadow'};
            }
            tooltip.trigger = "axis";
        }
        options.tooltip = tooltip;
    }

    // 工具栏
    if(startOptions.showToolbox) {
        options.toolbox = {};
        let feature = {};
        if (startOptions.enableMyRestoreTool) {
            feature.myRestore = {
                title: "还原",
                icon: 'M3.8,33.4 M47,18.9h9.8V8.7 M56.3,20.1 C52.1,9,40.5,0.6,26.8,2.1C12.6,3.7,1.6,16.2,2.1,30.6 M13,41.1H3.1v10.2 M3.7,39.9c4.2,11.1,15.8,19.5,29.5,18 c14.2-1.6,25.2-14.1,24.7-28.5',
                onclick: function (ecModel, api) {
                    // 获取当前Chart的数据集
                    let ds = api.getOption().dataset;
                    let dsLen = ds.length > 0 && ds[0].source? ds[0].source.length : 0;

                    // 清除区域缩放的历史数据
                    for (let p in ecModel) {
                        if (p.startsWith("__ec_inner_") && ecModel[p]['snapshots']) {
                            ecModel[p]['snapshots'] = null;
                            break;
                        }
                    }

                    // 触发原生的 restore 操作
                    api.dispatchAction({
                        type: 'restore',
                        from: this.uid
                    });

                    // 若调用原生Restore后，图表没有数据，则用ds重绘图表
                    let instance = echarts.getInstanceByDom(api.getDom());
                    let _ds = instance.getOption().dataset;
                    let _dsLen = _ds.length > 0 && _ds[0].source? _ds[0].source.length : 0;
                    if (dsLen > _dsLen) {
                        instance.setOption({dataset: ds});
                    }
                }
            };
        } else {
            feature.restore = {};
        }
        feature.saveAsImage = {};

        if ("pie" !== chartType) {
            let _feature = {};
            if (startOptions.showMagicType) {
                _feature.magicType = {  type: ['line', 'bar'] };
            }
            _feature.dataZoom = {yAxisIndex: 'none'};

            feature = {
                ..._feature,
                ...feature
            }
            options.toolbox.right = 10;
        }
        options.toolbox.feature = feature;
    }

    // 折线图 & 柱状图 特有的功能
    switch(chartType) {
        case "line":
            // 计算系列的最小值与最大值
            let mmValue = EchartsUtil.extreme(dataset);
            let min = options.min = mmValue[0];
            let max = options.max = mmValue[1];

            // 图表渐变色
            if (startOptions.enableGradient) {
                options.visualMap = {
                    show: false,
                    type: 'continuous', // 渐变色
                    inRange: {
                        color: ["#f6efa6", "#d88273", "#bf444c"]
                    },
                    min: min || 0,
                    max: max || 1
                };
                options.color = standardOptions.color || startOptions.color || ['#B95658'];
            }

            // X轴 & Y轴 添加虚线框、X轴零间隙
            options.xAxis = {
                boundaryGap,
                splitLine: {show: true, lineStyle: {type: 'dashed'}}
            };
            options.yAxis = {
                min,
                splitLine: {show: true, lineStyle: {type: 'dashed'}}
            };
        case "bar":
            // DataZoom区域放大
            let dataZoom = [];
            if (startOptions.showZoomBar) {
                dataZoom.push({type: 'slider'})
            }
            if (startOptions.enableZoom) {
                dataZoom.push({type: 'inside'})
            }
            options.dataZoom = dataZoom;

            // X轴 & Y轴 设置
            options.xAxis = options.xAxis || {};
            options.xAxis.type = 'category';
            options.xAxis.axisLabel = {showMaxLabel: true};
            options.yAxis = options.yAxis || {};
            if (startOptions.unit && startOptions.showAxisUnit) {
                options[startOptions.inverse?"xAxis":"yAxis"].axisLabel = {formatter: '{value}' + startOptions.unit};
            }
            if (startOptions.axisName) {
                options[startOptions.inverse?"xAxis":"yAxis"].name = startOptions.axisName;
            }
            break;
    }

    // 图表反转，多用于条形图
    if (startOptions.inverse) {
        options.xAxis = options.xAxis || {};
        options.yAxis = options.yAxis || {};

        options.xAxis.type = 'value';
        options.yAxis.type = 'category';
        options.yAxis.inverse = true;
    }

    // 图表标题
    if (startOptions.title) {
        options.title = {text: startOptions.title, left: "center"};
    }

    // Y轴极值设置
    if (null != startOptions.ymin) {
        if (options.yAxis.min) {
            if (startOptions.ymin < options.yAxis.min) {
                options.yAxis.min = startOptions.ymin;
            }
        } else {
            options.yAxis.min = startOptions.ymin;
        }
    }
    if (null != startOptions.ymax) {
        if (options.yAxis.max) {
            if (startOptions.ymax > options.yAxis.max) {
                options.yAxis.max = startOptions.ymax;
            }
        } else {
            options.yAxis.max = startOptions.ymax;
        }
    }

    // 图表边距
    options.grid = {
        left:"9%",
        top:60,
        right:"8%",
        bottom:30
    };

    /*
     * 合并标准化参数，并返回完整的图表参数
     */
    standardOptions.backgroundColor = 'transparent'
    options = echarts.util.merge(options, standardOptions, true)
    return options;
};

/**
 * 初始化基本Init Option
 */
EchartsUtil.initOption = function () {
    return {backgroundColor: 'transparent'};
};

/**
 * 构建Dataset数据集对象
 *
 * @param dimensions 维度
 * @param source 数据
 */
EchartsUtil.buildDataset = function(dimensions, source) {
    return {
        dimensions,
        source: source || []
    };
};

/**
 * 计算数据集的系列最小值
 *
 * @param dataset 数据集
 */
EchartsUtil.min = function(dataset) {
    return EchartsUtil.extreme(dataset)[0];
};

/**
 * 计算数据集的系列最大值
 *
 * @param dataset 数据集
 */
EchartsUtil.max = function(dataset) {
    return EchartsUtil.extreme(dataset)[1];
};


/**
 * 计算数据集系列的最小值和最大值
 *
 * @param dataset 数据集
 */
EchartsUtil.extreme = function(dataset) {
    let min = null;
    let max = null;
    if (dataset && dataset.dimensions && dataset.source) {
        let dlen = dataset.dimensions.length;
        if (dlen > 1 && dataset.source.length > 0) {
            for (let i = 1; i < dlen; i++) {
                let propertyName = dataset.dimensions[i].name;
                for (let item of dataset.source) {
                    let val = item[propertyName];
                    min = min || val;
                    max = max || val;

                    min = min > val? val : min;
                    max = max < val? val : max;
                }
            }

            // Min、Max缩放处理，让图表更好看
            min = min || 0;
            max = max || 0;
            min = Math.floor(min * (min >= 0? 0.9 : 1.1)); // 最小值缩小10%
            max = Math.ceil(max * 1.1); // 最大值放大10%
        }
    }
    return [min, max];
};

/**
 * 设置图表的最大值与最小值
 *
 * @param option  图表Option
 */
EchartsUtil.setExtreme = function(option) {
    let mmVal = EchartsUtil.extreme(option.dataset);
    if (option.visualMap) {
        option.visualMap.min = mmVal[0];
        option.visualMap.max = mmVal[1];
    }
    if (option.yAxis) {
        if (isNotBlank(option.yAxis.min)) {
            option.yAxis.min = option.yAxis.min > mmVal[0]? mmVal[0] : option.yAxis.min;
        } else {
            option.yAxis.min = mmVal[0];
        }

        if (isNotBlank(option.yAxis.max)) {
            option.yAxis.max = option.yAxis.max < mmVal[1]? mmVal[1] : option.yAxis.max;
        } else {
            option.yAxis.max = mmVal[1];
        }
    }
}

/**
 * 折线图构建器
 */
EchartsUtil.Line = {
    build(dataset, startOptions, standardOptions) {
        return EchartsUtil.build('line', dataset, startOptions, standardOptions)
    }
}

/**
 * 柱状图构建器
 */
EchartsUtil.Bar = {
    build(dataset, startOptions, standardOptions) {
        return EchartsUtil.build('bar', dataset, startOptions, standardOptions)
    }
}

/**
 * 饼图构建器
 */
EchartsUtil.Pie = {
    build(dataset, startOptions, standardOptions) {
        return EchartsUtil.build('pie', dataset, startOptions, standardOptions)
    }
}

/**
 * 桑基图构建器
 */
EchartsUtil.Sankey = {
    format({data, key, textKey, valKey, parentKey='parentId', rootNodeId='0'} = {}) {
        const sankeyData = [];
        const names = [];
        if (data instanceof Array && data.length > 0) {
            // 构建Map结构
            let dataMap = {};
            for (let item of data) {
                dataMap[item[key]] = item;
            }

            // 构建图表数据
            for (let item of data) {
                if (item[parentKey] && rootNodeId !== item[parentKey]) {
                    let parentItem = dataMap[item[parentKey]];
                    if (parentItem) {
                        sankeyData.push({source: parentItem[textKey], target: item[textKey], value: item[valKey]});
                    }
                }
            }
            const namesMap = {};
            for (let item of sankeyData) {
                namesMap[item.source] = 1;
                namesMap[item.target] = 1;
            }
            for (let p in namesMap) {
                names.push({name: p});
            }
        }
        return {data: names, links: sankeyData};
    },
    build({data, key, textKey, valKey, parentKey='parentId', rootNodeId='0', unit=''} = {}, options = {}) {
        const sankeyDataObj = EchartsUtil.Sankey.format({data, key, textKey, valKey, parentKey, rootNodeId});
        options = Object.assign({}, {
            series: {
                type: "sankey",
                layout: "none",
                emphasis: {focus: "adjacency"},
                data: sankeyDataObj.data,
                links: sankeyDataObj.links
            },
            label: {
                formatter: (params) => {
                    let label = params.name
                    let value = params.value ?? -1;
                    if (value >= 0) {
                        value = Intl.NumberFormat().format(value);
                        label += ' ' + value + unit;
                    }
                    return label;
                }
            },
            tooltip: {trigger: "item", triggerOn: "mousemove"},
            backgroundColor: 'transparent'
        }, options);
        return options;
    }
}

/**
 * 雷达图构建器
 */
EchartsUtil.Radar = {
    build(indicator = [], data = [], option = {}, showLabel = true) {
        if (showLabel) {
            for (let item of data) {
                item.label = {show: true}
            }
        }
        option = Object.assign({}, {
            backgroundColor: 'transparent',
            radar: {
                indicator,
                axisName: {
                    fontWeight: 'bold'
                },
                radius: '84%'
            },
            series: {
                type: 'radar',
                emphasis: {
                    lineStyle: {
                        width: 4
                    }
                },
                data
            }
        }, option);
        return option;
    }
}

/**
 * 仪表盘构建器
 */
EchartsUtil.Gauge = {
    build(data = [], {color = null, suffix = '', min = 0, max = 100, radius = '95%', titleFontSize = null, fontColor = 'inherit', fontSize = 20, fontWeight = 'bold', tooltip = false, progress = true, step = 10} = {}, option) {
        max = EchartsUtil.Gauge.fitMax(max, step);
        option = Object.assign({}, {
            backgroundColor: 'transparent',
            series: [{
                type: 'gauge',
                min,
                max,
                radius,
                center: ['50%', '52%'],
                progress: {show: progress},
                detail: {
                    valueAnimation: true,
                    offsetCenter: [0, '55%'],
                    formatter: '{value|{value}}' + '{suffix|' + suffix + '}',
                    rich: {
                        value: {
                            color: fontColor,
                            fontWeight,
                            fontSize,
                        },
                        suffix: {
                            color: fontColor,
                            fontSize: fontSize * 0.65,
                            padding: [0, 0, 0, 5]
                        }
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: [[1, '#E8EBF0']]
                    }
                },
                itemStyle: {
                    color
                },
                data
            }]
        }, option);
        if (!option.series[0].splitNumber && max) {
            option.series[0].splitNumber = Math.ceil(max / step);
        }
        if (tooltip) {
            option.series[0].tooltip = {formatter: '{a} <br/>{b} : {c}%'};
        }
        if (titleFontSize) {
            option.series[0].title = Object.assign({}, option.series[0].title, {fontSize: titleFontSize})
        }
        return option;
    },
    fitMax(max, step) {
        let mod = max % step;
        if (mod > 0) {
            max += (step - mod);
        }
        return max;
    },
    setMax(option, max, step) {
        max = EchartsUtil.Gauge.fitMax(max, step);
        option.series[0].max = max;
        option.series[0].splitNumber = Math.ceil(max / step);
    },
    setValue(option, val) {
        option.series[0].data = val;
    }
}

/**
 * 地图构建器
 */
EchartsUtil.Map = {
    build(opts, callback) {
        opts = Object.assign({}, {
            ctxPath: '',
            adCode: '100000',
            roam: true,
            zoom: 1,
            layoutCenter: ['50%', '50%'],
            layoutSize: '100%',
            symbolSize: 10,
            showLabel: true,
            geoUrl: null,
            points: []
        }, opts)
        let map = new EchartsUtil.Map._map();
        map.create(opts, callback);
    },
    addPoint(item, option) {
        option.series[0].data.push(item);
    },
    points(data, option) {
        option.series[0].data = data;
    },

    /**
     * 地图类
     */
    _map: function () {
        // 内部别名映射
        this.alias = {"100000": "china"};

        /**
         * 创建地图
         */
        this.create = function (opts, callback) {
            opts.mapName = this.alias[opts.adCode] || opts.adCode;
            this.readGeoData(opts.ctxPath, opts.adCode, opts.geoUrl).then(data => {
                echarts.registerMap(opts.mapName, data); // 注册地图
                let option = this.buildOption(opts);
                callback && callback(option);
            })
        };

        /**
         * 读取Geo地图数据
         */
        this.readGeoData = async function (ctxPath, adCode, geoUrl) {
            geoUrl = geoUrl || (ctxPath + '/data/geo/' + adCode + '_full.json');
            const response = await fetch(geoUrl);
            const data = await response.json();
            data.features.forEach(item => {
                item.properties.name = item.properties.name.replace(/省|市|区|特别行政区|自治区|自治州|自治县|维吾尔|壮族/g, "")
            });
            return data;
        };

        /**
         * 构建ECharts Option
         */
        this.buildOption = function (opts) {
            return {
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'item',
                    transitionDuration: 0.2,
                    formatter: (params) => {
                        if (params.componentSubType === 'map') {
                            return params.name
                        } else if (params.componentSubType === 'effectScatter') {
                            if (opts.tformatter) {
                                return opts.tformatter(params);
                            }
                            return params.name;
                        }
                    }
                },
                geo: {
                    map: opts.mapName,               // 使用注册的地图
                    roam: opts.roam,                 // 是否开启使用鼠标或触摸进行漫游（移动和缩放）
                    center: opts.center,             // 当前视角的可视区的中心
                    zoom: this.zoomV(opts),          // 当前视角的缩放比例
                    layoutCenter: this.layoutCenter(opts), // 定义区域中心在容器中的位置
                    layoutSize: opts.layoutSize,     // 地图的大小
                    label: {
                        show: opts.showLabel,
                        color: '#00BCD4'
                    },
                    itemStyle: {
                        areaColor: 'transparent', // 地图区域的颜色
                        borderColor: '#00BCD4',   // 图形的描边颜色
                        borderWidth: 1.1,         // 描边线宽
                    },
                    emphasis: { // 高亮状态下的多边形和标签样式
                        label: {
                            color: '#00BCD4'      // 文字的颜色
                        },
                        itemStyle: {
                            areaColor: "#023827"  // 地图区域的颜色
                        }
                    }
                },
                series: [{
                    type: 'effectScatter',    // 带有涟漪特效动画的散点（气泡）图
                    coordinateSystem: 'geo',  // 指定坐标系为：地理坐标系
                    rippleEffect: {           // 涟漪特效相关配置
                        scale: 4,             // 动画中波纹的最大缩放比例
                        brushType: 'stroke',  // 波纹的绘制方式
                    },
                    itemStyle: {
                        color:'#FFC107' // 图形颜色
                    },
                    label: {
                        show: false,
                        color: '#00BCD4',
                        position: 'bottom',
                        formatter: '{b}'
                    },
                    emphasis: { // 鼠标悬停时
                        label: {
                            show: true
                        },
                    },
                    symbolSize: opts.symbolSize,
                    data: opts.points
                }]
            }
        };

        /**
         * 获取Zoom值
         */
        this.zoomV = function (opts) {
            let zoom = opts.zoom;
            if (this.alias[opts.adCode]) {
                zoom = zoom < 1.3? 1.3 : zoom;
            }
            return zoom;
        };

        /**
         * 获取LayoutCenter
         */
        this.layoutCenter = function (opts) {
            return this.alias[opts.adCode]? ['50%', '68%'] : opts.layoutCenter;
        };
    }
}

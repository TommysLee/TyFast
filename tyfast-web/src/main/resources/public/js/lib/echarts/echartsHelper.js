/*
 * Baidu ECharts 图表辅助类，用于快速构建图形化控件
 * @Author TyCode
 */

function EChartsHelper() {};
EChartsHelper.lineDistance = [40, 0];

/**
 * 构建ECharts图表参数（支持折线图、柱状图、饼图）
 *
 * @param chartType 图表类型
 * @param dataset 数据集
 * @param startOptions 常用功能一键启动参数
 * @param standardOptions 官方标准化参数（标准化参数优先级高于一键启动参数）
 */
EChartsHelper.buildOptions = function(chartType, dataset, startOptions, standardOptions) {
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
        showZoomBar: false,   // 是否显示 X 轴 DataZoom Bar，默认：false
        enableZoom: true,     // 是否启用 X 轴 区域放大功能，默认：true
        enableGradient: true, // 是否启用图表渐变色（仅对折线图有效），默认：true
        enableMyRestoreTool: false, // 启用自定义的Restore工具，以替换原生的Restore工具
        title: null,    // 图表标题
        unit: null,     // 系列值的显示单位
        axisName: null, // Y轴标题
        lines: []       // Y轴自定义划线，每个Line的格式说明：{pos: Y轴坐标值, text: 显示的文本, color: 线颜色}
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
        options.legend = legend;
    }

    // 系列 和 最大值Point & 最小值Point & 均值线 & 自定义线
    let series = {type: chartType};
    switch(chartType) {
        case "line":
            series.showSymbol = startOptions.showSymbol;
            series.areaStyle = startOptions.showArea? {} : null;
            series.markLine = {silent: true};
        case "bar":
            // 最大值Point & 最小值Point
            let pointData = [];
            if (startOptions.showMaxPoint) {
                pointData.push({ type: 'max', name: 'Max' });
            }
            if (startOptions.showMinPoint) {
                pointData.push({ type: 'min', name: 'Min' });
            }
            series.markPoint = {data: pointData};

            // 均值线 & 自定义线
            let lineData = [];
            if (startOptions.showAvgLine) {
                let lineStyle = dlen > 2? null : {color: '#F72C5B'};
                lineData.push(...[{type: 'average'}, {type: 'average', lineStyle, label: {formatter: '平均值', position: 'start', distance: EChartsHelper.lineDistance}}]);
            }

            for (let line of startOptions.lines) {
                let d = {yAxis: line.pos};
                if (line.text) {
                    d.label = {formatter: line.text, position: 'start', distance: EChartsHelper.lineDistance};
                }
                if (line.color) {
                    d.lineStyle = {color: line.color};
                }
                lineData.push(...[{yAxis: d.yAxis}, d]);
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
    for (let i = 1; i < dlen; i++) {
        let _series = {...series};
        let dim = dataset.dimensions[i];
        if (dim.style) { // 若Dataset中指定了图表类型，则优先使用，否则，使用默认图表类型
            _series.type = dim.style;
            boundaryGap = "line" !== dim.style? true : boundaryGap;
        }
        options.series.push(_series);
    }

    // Tooltip
    if (startOptions.showTooltip) {
        let tooltip = {};
        if (startOptions.unit) {
            tooltip.valueFormatter = value => (value || value == 0 ? value : "") + startOptions.unit;
        }
        if ("pie" === chartType) {
            tooltip.trigger = "item";
        } else {
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
                        EChartsHelper.refresh(instance, ds);
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
            let mmValue = EChartsHelper.extreme(dataset);
            let min = mmValue[0];
            let max = mmValue[1];

            // 图表渐变色
            if (startOptions.enableGradient) {
                options.visualMap = {
                    show: false,
                    type: 'continuous', // 渐变色
                    min,
                    max
                };
                options.color = ['#B95658'];
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
                options.yAxis.axisLabel = {formatter: '{value}' + startOptions.unit};
            }
            if (startOptions.axisName) {
                options.yAxis.name = startOptions.axisName;
            }
            break;
    }

    // 图表标题
    if (startOptions.title) {
        options.title = {text: startOptions.title, left: "center"};
    }

    /*
     * 合并标准化参数，并返回完整的图表参数
     */
    options = echarts.util.merge(options, standardOptions, true)
    return options;
};

/**
 * 构建Dataset数据集对象
 *
 * @param dimensions 维度
 * @param source 数据
 */
EChartsHelper.buildDataset = function(dimensions, source) {
    let dataset = {
        dimensions,
        source: source || []
    };
    return dataset;
};

/**
 * 计算数据集的系列最小值
 *
 * @param dataset 数据集
 */
EChartsHelper.min = function(dataset) {
    return EChartsHelper.extreme(dataset)[0];
};

/**
 * 计算数据集的系列最大值
 *
 * @param dataset 数据集
 */
EChartsHelper.max = function(dataset) {
    return EChartsHelper.extreme(dataset)[1];
};


/**
 * 计算数据集系列的最小值和最大值
 *
 * @param dataset 数据集
 */
EChartsHelper.extreme = function(dataset) {
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
            min = Math.floor((min || 0) * 0.9); // 最小值缩小10%
            max = Math.ceil((max || 0) * 1.1); // 最大值放大10%
        }
    }
    return [min, max];
};

/**
 * 实例化 ECharts
 *
 * @param dom Dom对象 或 Dom ID
 * @param chartType 图表类型
 * @param dataset 数据集
 * @param theme 主题
 * @param startOptions 常用功能一键启动参数
 * @param standardOptions 官方标准化参数（标准化参数优先级高于一键启动参数）
 */
EChartsHelper.init = function(dom, chartType, dataset, theme, startOptions, standardOptions) {
    dom = typeof(dom) === 'string'? document.querySelector(dom): dom;
    let options = EChartsHelper.buildOptions(chartType, dataset, startOptions, standardOptions);
    let instance = echarts.init(dom, theme || null);
    EChartsHelper.registerResizeEvent(instance);
    instance.setOption(options);
    return instance;
};

/**
 * 显示Loading提示
 *
 * @param instance ECharts实例
 */
EChartsHelper.showLoading = function (instance, text) {
    instance && instance.showLoading({text: text || '正在加载中...', maskColor: 'rgba(255, 255, 255, 0.3)'});
};

/**
 * 隐藏Loading提示
 *
 * @param instance ECharts实例
 */
EChartsHelper.hideLoading = function (instance) {
    instance && instance.hideLoading();
};

/**
 * 注册Resize事件，实现图表自适应
 *
 * @param instance ECharts实例
 */
EChartsHelper.registerResizeEvent = function (instance) {
    window.addEventListener("resize", () => {
        instance.resize();
    })
};

/**
 * 更新图表
 *
 * @param instance ECharts实例
 * @param option ECharts选项
 * @param range visualMap 的最小值与最大值 或 传值true，则自动计算其值
 * @param notMerge 是否不跟之前设置的 option 进行合并，默认：false
 */
EChartsHelper.refresh = function (instance, option, range, notMerge) {
    let dataset = option.dataset;
    range = range || false;
    notMerge = notMerge || false;

    if (notMerge) {
        let _option = instance.getOption();
        for (var p in option) {
            _option[p] = option[p];
        }
        option = _option;
    }

    if (range) {
        if (typeof(range) === 'boolean' && true === range) {
            range = EChartsHelper.extreme(dataset);
        }

        // 视觉映射：渐变色更新
        let visualMap = instance.getOption().visualMap;
        for (let v of visualMap) {
            v.min = range[0];
            v.max = range[1];
            v.range = range;
        }
        option.visualMap = visualMap;

        // Y轴 最小值更新
        let yAxis = instance.getOption().yAxis;
        for (let y of yAxis) {
            y.min = range[0];
        }
        option.yAxis = yAxis;
    }

    // 若维度变化，则增加或减少系列
    let series = instance.getOption().series;
    let padding = dataset.dimensions.length - series.length - 1;

    if (padding !== 0) {
        if (padding > 0) {
            delete series[0].id;
            for (let i = 0; i < padding; i++) {
                series.push(series[0]);
            }
        } else {
            series.splice(0, Math.abs(padding));
        }
        option.series = series;
    }

    // 刷新图表
    EChartsHelper.hideLoading(instance);
    instance.setOption(option, notMerge);
};

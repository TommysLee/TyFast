/**
 * 飞线图
 *
 * @param obj dom ID 或 ECharts实例
 * @param geoCode
 * @param params 参数集合
 */
function FlyLine(obj, geoCode, params) {
    // ECharts实例对象
    this.instance = typeof(obj) === 'string'? echarts.init(document.querySelector(obj)) : obj;

    // ECharts实例化参数
    this.options = {};

    // 内部别名映射
    this.alias = {"100000": "china"};

    // Geo 数据服务器
    this.geoServerUrl = (ctx||'/') + "js/lib/flyline/data/#code#_full.json";

    // Geo Code
    this.geoCode = geoCode;

    // Geo数据
    this.geoData = {};

    // 钻取队列
    this.queue = [];

    // 钻取深度
    this.maxDepth = 3;

    // 默认参数
    params = Object.assign({}, {
        points: null,       // 气泡图数据
        lines: null,        // 飞线数据
        centerIndex: 0,     // 中心点索引
        centerPoint: null,  // 中心点
        roam: true,         // 是否开启地图缩放与拖动功能
        autofit: true,      // 是否开启图自适应显示
        tooltip: false,     // 是否开启Tooltip
        simplify: true,     // 地区名称是否做简化处理
        showPointName: false,  // 是否显示点名称
        excludePointIndex: -1, // 排除的点索引 (一般为中心点)
        enableDrill: false, // 是否开启上钻与下钻功能
        inverse: false,     // 反转飞线方向
        title: null,        // 图标题
        data: null          // 对应的业务数据
    }, params);

    /**
     * 读取Geo地图数据
     */
    this.readGeoDataMap = function () {
        this.showLoading();
        const xhr = new XMLHttpRequest();
        xhr.open('get',this.getGeoUrl(),false)
        xhr.send(null);
        let geodata = JSON.parse(xhr.responseText)
        this.doSimpleName(geodata);
        this.hideLoading();
        this.geoData = geodata;
        this.data = params.data || geodata.features.map(val => {
            return {
                name: val.properties.name,
                value: val.properties.adcode
            }
        })
        return geodata;
    };

    /**
     * 构建 “气泡图” 数据
     */
    this.buildPoints = function () {
        if (!params.points) {
            params.points = this.geoData.features.map(val => {
                return {
                    name: val.properties.name,
                    value: val.properties.center
                }
            })
        }
        if (params.centerPoint) {
            this.centerPoint = params.centerPoint;
        } else {
            this.centerPoint = params.centerPoint = params.points[params.centerIndex];
            params.excludePointIndex = params.centerIndex;
        }
        return params.points = params.points.filter((item, index) => index != params.excludePointIndex);
    };

    /**
     * 构建 “飞线” 数据
     */
    this.buildLines = function () {
        if (!params.lines) {
            params.lines = [];
            params.points.forEach((v, index) => {
                if (v.value !== undefined) {
                    if (params.inverse) {
                        params.lines.push({coords: [this.centerPoint.value, v.value]})
                    } else {
                        params.lines.push({coords: [v.value, this.centerPoint.value]})
                    }
                }
            })
        }
        return params.lines;
    };

    /**
     * 构建 ECharts实例化参数
     */
    this.buildOptions = function () {
        // 加载地图数据
        this.readGeoDataMap();

        // 注册地图
        let mapName = this.alias[this.geoCode];
        mapName = mapName? mapName : this.geoCode;
        echarts.registerMap(mapName, this.geoData);

        // 构建 点&线 数据
        this.buildPoints() && this.buildLines();

        // 参数
        this.options = {
            backgroundColor: 'transparent',
            tooltip: {
                trigger: 'item',
                showDelay: 0,
                transitionDuration: 0.2,
                formatter: (params) => {
                    if (params.componentSubType === 'map') {
                        return params.name
                    }
                }
            },
            geo: {
                map: mapName,
                zlevel: 10,
                show: true,
                roam: params.roam,
                zoom: this.zoomV(),
                layoutCenter: this.layoutCenter(),
                layoutSize: "100%",
                label: {
                    show: true,
                    fontSize: 12,
                    color: '#43D0D6'
                },
                itemStyle: {
                    color: '#062031',
                    borderWidth: 1.1,
                    borderColor: '#43D0D6'
                },
                emphasis: {
                    label: {
                        show: false
                    },
                    itemStyle: {
                        color: "#023827"
                    }
                }
            },
            series: [
                {
                    name: params.title,
                    type: 'map',
                    geoIndex: 0,
                    selectedMode: false,
                    data: this.data
                },
                {
                    type: 'effectScatter',
                    coordinateSystem: 'geo',
                    zlevel: 15,
                    symbolSize: 8,
                    rippleEffect: {
                        period: 4,
                        brushType: 'stroke',
                        scale: 4
                    },
                    itemStyle: {
                        color:'#FFB800',
                        opacity: 1
                    },
                    label: {
                        show: params.showPointName,
                        color: '#43D0D6',
                        formatter: "{b}"
                    },
                    data: params.points
                },
                {
                    type:'effectScatter',
                    coordinateSystem: 'geo',
                    zlevel: 15,
                    symbolSize:12,
                    rippleEffect: {
                        period: 6, brushType: 'stroke', scale: 8
                    },
                    itemStyle:{
                        color:'#FF5722',
                        opacity:1
                    },
                    label: {
                        show: params.showPointName,
                        color: '#43D0D6',
                        formatter: "{b}"
                    },
                    data: [this.centerPoint]
                },
                {
                    type: 'lines',
                    coordinateSystem: 'geo',
                    zlevel: 15,
                    effect: {
                        show: true,
                        period: 5,
                        trailLength: 0,
                        symbol: 'arrow',
                        color:'#01AAED',
                        symbolSize: 8
                    },
                    lineStyle: {
                        opacity: 0.6,
                        curveness: 0.2,
                        color: '#FFB800'
                    },
                    data: params.lines
                }
            ]
        };
        return this.options;
    };

    /**
     * 渲染 ECharts图
     */
    this.render = function() {
        this.instance.setOption(this.buildOptions());
        if (params.autofit && !this.fited) {
            window.addEventListener("resize", () => {
                this.instance.resize();
            })
            this.fited = true;
        }
        if (params.enableDrill) {
            this.enableDrill();
        }
    };

    /**
     * 重置 ECharts图
     */
    this.reset = function (code) {
        if (code) {
            params.points = params.lines = params.centerPoint = null;
            this.geoCode = code;
            this.render();
        }
    };

    /**
     * 启用地图钻取功能
     */
    this.enableDrill = function () {
        if (!this.drilled) {
            this.instance.on('click', 'series.map', (params) => {
                this.drillDown(params.value)
            });
            this.instance.getZr().on('click', (event) => {
                if (!event.target) {
                    this.drillUp()
                }
            });
            this.drilled = true;
        }
    };

    /**
     * 向上钻取
     */
    this.drillUp = function () {
        this.reset(this.queue.pop());
    };

    /**
     * 向下钻取
     */
    this.drillDown = function (code) {
        if (this.queue.length + 1 < this.maxDepth) {
            this.queue.push(this.geoCode);
            this.reset(code)
        }
    };

    /**
     * 获取当前Code的Geo服务URL
     */
    this.getGeoUrl = function () {
        return this.geoServerUrl.replace(/#code#/g, this.geoCode);
    };

    /**
     * 地区名称做简化处理
     */
    this.doSimpleName = function (geodata) {
        if (params.simplify) {
            (geodata || this.geoData).features.forEach(v => {
                v.properties.name = v.properties.name.replace(/省|市|区|特别行政区|自治区|维吾尔|壮族/g, "")
            })
        }
    };

    /**
     * 获取Zoom值
     */
    this.zoomV = function () {
        return this.alias[this.geoCode]? 1.3 : 1;
    };

    /**
     * 获取LayoutCenter
     */
    this.layoutCenter = function () {
        return this.alias[this.geoCode]? ['50%', '68%'] : ['50%', '50%'];
    };

    /**
     * 显示Loading提示
     */
    this.showLoading = function () {
        this.instance && this.instance.showLoading({text: '正在加载中...', showSpinner:true, maskColor: 'rgba(255, 255, 255, 0.3)', zlevel:99});
    };

    /**
     * 隐藏Loading提示
     */
    this.hideLoading = function () {
        this.instance && this.instance.hideLoading();
    };
}
/**
 * 简单、实用的SVG API
 * @Author TyCode
 */
class SimpleSVG {

    // 默认显示比例
    ratio = 1;

    // 放大与缩小的步长
    step = 0.1;

    // 最小缩放比例
    minRatio = 0.3;

    // 最大缩放比例
    maxRatio = 5;

    /**
     * 构造函数
     *
     * @param selector CSS选择器：SVG的Div容器
     * @param url SVG文件的URL
     * @param call SVG加载成功后的回调函数
     * @param ratio SVG的初始显示比例，默认为：1
     * @param step SVG缩放功能的步长，单位：百分比，小数形式
     * @param minRatio 最小缩放比例，默认：0.3
     * @param maxRatio 最大缩放比例，默认：5
     */
    constructor(selector, url, callback, ratio, step, minRatio, maxRatio) {
        this.ratio = ratio || this.ratio;
        this.step = step || this.step;
        this.minRatio = minRatio || this.minRatio;
        this.maxRatio = maxRatio || this.maxRatio;

        // Snap.SVG对象
        this.snap = Snap(selector);

        // 异步加载SVG资源
        Snap.load(url, (fragment) => {
            this.snap.append(fragment); // 将SVG添加到容器
            this.svg = this.snap.select("svg").drag(); // 获取SVG对象，并启用拖拽功能

            // SVG容器绑定鼠标滚动事件
            this.bindWheel(this.snap.node);

            // 初始化SVG图像默认比例
            this.scale(this.ratio);

            // 调用回调函数
            callback = callback || function() {};
            callback(this.svg, this.snap);
        });
    }

    /**
     * 绑定Html5 wheel事件，实现鼠标滚轮缩放SVG图像
     * (向上滚动：放大；向下滚动：缩小)
     *
     * @param domEl dom元素对象
     */
    bindWheel(domEl) {
        this.wheelEvent = (e) => {
            // 阻止事件冒泡/默认事件
            e.preventDefault();

            // deltaY 小于0，代表向上滚动，步长为正值
            this.step = e.deltaY < 0? Math.abs(this.step) : Math.abs(this.step) * -1;

            // 计算缩放比例
            this.ratio += this.step;

            // 触发缩放操作
            this.scale();
        };
        domEl.addEventListener("wheel", this.wheelEvent);
    }

    /**
     * 缩放SVG图像
     *
     * @param ratio 缩放比例
     */
    scale(ratio) {
        this.ratio = ratio || this.ratio;

        // 缩放比例控制在 (minRatio, maxRatio) 之间
        if (this.ratio > this.minRatio && this.ratio < this.maxRatio) {
            let matrix = this.svg.attr("transform").localMatrix; // SVG Transform矩阵
            let dx = matrix.e; // 水平位移
            let dy = matrix.f; // 垂直位移

            // 缩放图像
            this.svg.attr({
                transform: "t" + dx + "," + dy + "s" + this.ratio + ",0,0"
            });
        } else {
            ratio = this.ratio + this.step;
            this.ratio = ratio < this.minRatio? this.minRatio : ratio > this.maxRatio? this.maxRatio : ratio;
        }
        return this;
    }

    /**
     * 放大SVG图像
     */
    zoomIn() {
        this.step = Math.abs(this.step);
        this.ratio += this.step;
        this.scale();
    }

    /**
     * 缩小SVG图像
     */
    zoomOut() {
        this.step = Math.abs(this.step) * -1;
        this.ratio += this.step;
        this.scale();
    }

    /**
     * 获取SVG元素
     *
     * @param selector
     */
    getElement(selector) {
        return this.svg.select(selector);
    }

    /**
     * 获取多个SVG元素
     *
     * @param selector
     */
    getAllElement(selector) {
        return this.svg.selectAll(selector);
    }

    /**
     * 设置SVG元素的文本内容
     *
     * @param selector
     * @param text
     */
    setText(selector, text) {
        this.getElement(selector).node.textContent = text || "";
        return this;
    }

    /**
     * 获取SVG元素的文本内容
     *
     * @param selector
     */
    getText(selector) {
        return this.getElement(selector).node.textContent;
    }

    /**
     * 显示SVG元素
     *
     * @param selector
     */
    show(selector) {
        this.getElement(selector).attr({opacity:1, 'fill-opacity':1});
        return this;
    }

    /**
     * 显示多个SVG元素
     *
     * @param selector
     */
    showAll(selector) {
        this.getAllElement(selector).attr({opacity:1, 'fill-opacity':1});
        return this;
    }

    /**
     * 隐藏SVG元素
     *
     * @param selector
     */
    hide(selector) {
        this.getElement(selector).attr({opacity:0, 'fill-opacity':0});
        return this;
    }

    /**
     * 隐藏多个SVG元素
     *
     * @param selector
     */
    hideAll(selector) {
        this.getAllElement(selector).attr({opacity:0, 'fill-opacity':0});
        return this;
    }

    /**
     * 切换SVG元素显隐
     *
     * @param selector
     */
    toggle(selector) {
        let opacity = this.getElement(selector).attr("opacity");
        opacity = null == opacity? this.getElement(selector).attr("fill-opacity") : opacity;
        opacity = isNaN(opacity)? 1 : parseFloat(opacity);
        if (opacity = opacity < 0.5) {
            this.show(selector);
        } else {
            this.hide(selector);
        }
        return this;
    }

    /**
     * 使SVG充满浏览器
     */
    fill() {
        this.svg.attr({preserveAspectRatio: "none"});
        return this;
    }

    /**
     * 禁用拖拽功能
     */
    disableDrag() {
        this.svg.undrag();
        return this;
    }

    /**
     * 启用拖拽功能
     */
    enableDrag() {
        this.svg.drag();
        return this;
    }

    /**
     * 禁用缩放功能
     */
    disableScale() {
        this.snap.node.removeEventListener("wheel", this.wheelEvent);
        return this;
    }

    /**
     * 启用缩放功能
     */
    enableScale() {
        this.bindWheel(this.snap.node);
        return this;
    }
}
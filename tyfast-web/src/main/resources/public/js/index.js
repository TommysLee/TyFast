// 初始化Vue
let app = new Vue({
  el: "#app",
  mixins,
  mounted() {
    this.$nextTick(() => {
      const flyline = new FlyLine("#map_container", "100000", {
        centerPoint: {name: '武汉', value: [114.298572, 30.584355]},
        excludePointIndex: 16,
        inverse: true,
      });
      flyline.render();
    })
  },
  methods: {
    zoom(flag) {
      console.log(flag? "放大":"还原");
    }
  }
});
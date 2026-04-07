// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      flyline: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.flyline = new FlyLine("#map_container", "100000", {
        centerPoint: {name: '武汉', value: [114.298572, 30.584355]},
        excludePointIndex: 16,
        inverse: true
      });
      this.flyline.render();
    })
  },
  methods: {
    zoom(flag) {
      console.log(flag? "放大":"还原");
    },
    refresh() {
      console.log("刷新面板...");
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');
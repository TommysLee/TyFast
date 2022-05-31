// 初始化Vue
let app = new Vue({
  el: "#app",
  methods: {
    zoom(flag) {
      console.log(flag? "放大":"还原");
    }
  }
});
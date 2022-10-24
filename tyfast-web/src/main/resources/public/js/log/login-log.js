// 初始化Vue
let app = new Vue({
  el: "#app",
  mixins,
  data: {
    menuName: "登录日志",
    // 查询条件
    param: {
      loginName: null,
      type: null
    },
    // 数据表格
    datatable: {
      headers: [
        { text: '序号', value:'index', align:"center", width: 60},
        { text: '时间', value:'logTime', align:"center", width: 180},
        { text: '账号', value:'loginName'},
        { text: 'IP地址', value:'ip'},
        { text: '操作系统', value:'os', align:"center"},
        { text: '类型', value:'type', align:"center"},
        { text: '操作', value:'operation', align:"center"}
      ],
      items: []
    },
    // 类型
    types: [
      {text: '登入', value:1},
      {text: '登出', value:2},
    ],
    assistHeight: 20
  },
  computed: {
    typesMap() {
      t(this.types);
      return toMap(this.types);
    }
  },

  mounted() {
    // 加载列表数据
    this.doQuery();

    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
      if (!app.$refs['delPermis']) {
        this.datatable.headers.remove(6);
      }
    })
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery(page) {
      if (!this.loading) {
        this.loading = true;
        this.scrollDTableTop();

        this.pagination.page = typeof(page) === 'number'? page : 1;
        this.param.page = this.pagination.page;
        doAjax(ctx + "log/login/list", this.param, (result) => {
          if (result.state) {
            let pageData = result.data;
            this.pagination.totalPages = pageData.pages; // 总页数
            this.datatable.items = addIndexPropForArray(pageData.data, this.pagination); // 数据集合
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 重置查询表单
     */
    resetQueryForm() {
      this.resetForm('queryForm');
      this.doQuery();
    },

    /*
     * 删除数据
     */
    doDelete(logId) {
      doAjaxGet(ctx + "log/login/del/" + logId, null, (result) => {
        if (result.state) {
          this.toast(this.$t("操作成功"));
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
});

// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "登录日志",
      // 查询条件
      orgId: null,
      selectedOrgIds: [],
      param: {
        loginName: null,
        type: null
      },
      // 数据表格
      datatable: {
        headers: [
          { title: '#', value:'index', align:"center", width: 60},
          { title: '时间', value:'logTime', align:"center", width: 180},
          { title: '账号', value:'loginName'},
          { title: 'IP地址', value:'ip'},
          { title: '操作系统', value:'os', align:"center"},
          { title: '类型', value:'type', align:"center"},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        total: 0
      },
      // 类型
      types: [
        {title: '登入', value:1},
        {title: '登出', value:2},
      ]
    }
  },
  computed: {
    typesMap() {
      t(this.types);
      return toMap(this.types);
    }
  },

  watch: {
    selectedOrgIds(val) {
      if (val?.length) {
        this.orgId = val[0];
      } else {
        this.orgId = null;
      }
      this.doQuery(1);
    }
  },

  mounted() {
    this.$nextTick(() => {
      this.selectedOrgIds = [this.tenantId];
      // 页面渲染完成后，计算辅助元素的总高度
      this.assistHeight = calcAssistHeight();
      if (!this.$refs['delPermis']) {
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
        page = page??this.getFitPage();
        if (this.pagination.page !== page) {
          this.pagination.page = page;
        } else { // 这里的逻辑是在page不变的情况下，依然刷新数据表
          this.pagination.page = 0;
          this.$nextTick(() => {
            this.pagination.page = page;
          })
        }
      }
    },

    /*
     * 查询数据表
     */
    doQueryTable() {
      if (this.orgId && this.pagination.page > 0) {
        this.loading = true;
        this.scrollDTableTop();
        this.param.page = this.pagination.page;
        this.param.pageSize = this.pagination.pageSize;

        doAjaxPost(this.url("/" + this.orgId + "/log/login/list"), this.param, (result) => {
          if (result.state) {
            let pageData = result.data;
            this.datatable.total = pageData.total; // 总记录数
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
    resetQueryForm(page) {
      this.resetForm('queryForm');
      this.doQuery(page);
    },

    /*
     * 删除数据
     */
    doDelete(logId) {
      this.method = "del";
      doAjaxGet(this.url("/" + this.orgId + "/log/login/del/" + logId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');

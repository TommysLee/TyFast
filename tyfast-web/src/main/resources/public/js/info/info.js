const prefix = "/info";
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "新闻资讯",
      editor: null,
      // 查询条件
      orgId: null,
      selectedOrgIds: [],
      param: {
        title: null
      },
      // 数据
      dataList: [],
      // 表单数据
      formData: {
        infoId: null,
        title: null,
        publishTime: null,
        content: null
      },
      // 抽屉窗口
      winDrawer: false,
      drawerTitle: ''
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
      this.assistHeight = calcAssistHeight(); // 计算辅助元素的总高度
      this.vnode = this.$refs.qcontainer;
    })
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery(page) {
      if (!this.loading) {
        if (page) {
          this.pagination.page = page;
        }
        this.loading = true;
        this.param.page = this.pagination.page;
        this.param.pageSize = this.pagination.pageSize;

        doAjax(this.url(`/${this.orgId}/${prefix}/list`), this.param, (result) => {
          if (result.state) {
            let pageData = result.data;
            this.pagination.pageCount = pageData.pages; // 总页数
            this.dataList = addIndexPropForArray(pageData.data, this.pagination); // 数据集合
            this.scrollTop();
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
      if (this.method !== 'update') {
        this.resetForm('queryForm');
      }
      this.doQuery(page);
    },

    /*
     * 打开表单编辑抽屉窗口
     */
    openWinDrawer(title, id) {
      this.formData.infoId = id || null;
      this.drawerTitle = title;
      this.winDrawer = true;
      this.initEditor();

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url(`/${this.orgId}/${prefix}/single/${id}`), null, (result) => {
          if (result.state) {
            this.mergeValue(this.formData, result.data);
            TinyEditor.setContent(this.editor, this.formData.content);
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 关闭抽屉窗口
     */
    closeWinDrawer() {
      this.winDrawer = false;
      this.editor && TinyEditor.setContent(this.editor, '')
      this.resetForm();
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      this.formData.content = this.editor.getSemanticHTML();
      this.method = this.formData.infoId? "update" : "save";
      doAjaxPost(this.url(`/${this.orgId}/${prefix}/${this.method}`), this.formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeWinDrawer();
          this.resetQueryForm();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(infoId) {
      this.method = "del";
      doAjaxGet(this.url(`/${this.orgId}/${prefix}/del/${infoId}`), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 初始化编辑器
     */
    initEditor() {
      if (!this.editor) {
        this.editor = Vue.markRaw(TinyEditor.init('#editor'));
      }
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');

/*
 * 用户的业务逻辑
 */
const userMixin = {
  data: function() {
    return {
      menuName: "用户管理",
      // 查询条件
      orgId: null,
      selectedOrgIds: [],
      param: {
        loginName: null,
        status: null
      },
      // 数据表格
      datatable: {
        headers: [
          { title: '#', value:'index', align:"center"},
          { title: '账号', value:'loginName'},
          { title: '用户类型', value:'usertype', align:"center"},
          { title: '姓名', value:'realName'},
          { title: '机构', value:'org.orgName'},
          { title: '状态', value:'status', align:"center"},
          { title: '最后登录IP', value:'loginIp', align:"center"},
          { title: '最后登录时间', value:'loginTime', align:"center", width:180},
          { title: '登录互踢', value:'enablekickout', align:"center"},
          { title: '创建时间', value:'createTime', align:"center", width:180},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        total: 0
      },
      // 表单数据
      formData: {
        userId: null,
        loginName: null,
        userType: 2,
        realName: null,
        gender: 0,
        phone: null,
        email: null,
        status: 0,
        remark: null
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: '',
      // 字典数据
      statusList: [],
      typeList: [],
      genderList: [],
      dictConfig: {
        'userstatus': 'statusList',
        'usertype': 'typeList',
        'gender': 'genderList'
      }
    }
  },

  computed: {
    statusMap: function() {
      t(this.statusList);
      return toMap(this.statusList);
    },
    typeMap: function () {
      t(this.typeList);
      return toMap(this.typeList);
    }
  },

  watch: {
    genderList(val) {
      t(val);
    },
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
      if (!this.$refs['updatePermis'] && !this.$refs['delPermis']) {
        this.datatable.headers.remove(10);
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

        doAjax(this.url("/" + this.orgId + "/system/user/list"), this.param, (result) => {
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
      if (this.method !== 'update') {
        this.resetForm('queryForm');
      }
      this.doQuery(page);
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id) {
      this.formData.userId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/" + this.orgId + "/system/user/single/" + id), null, (result) => {
          if (result.state) {
            this.mergeValue(this.formData, result.data);
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 关闭表单编辑画面
     */
    closeFormDialog() {
      this.winDialog = false;
      this.resetForm();
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      this.method = this.formData.userId? "update" : "save";
      doAjax(this.url("/" + this.orgId + "/system/user/" + this.method), this.formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeFormDialog();
          this.resetQueryForm();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(userId) {
      this.method = "del";
      doAjaxGet(this.url("/" + this.orgId + "/system/user/del/" + userId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 重置密码
     */
    doResetPwd(userId) {
      this.method = "reset";
      doAjaxGet(this.url("/" + this.orgId + "/system/user/password/reset/" + userId), null, (result) => {
        if (result.state) {
          this.toast("密码重置成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 更改登录互踢设置
     */
    changeKickOut(val, item) {
      this.method = "kickout";
      item.loading = true;
      doAjaxGet(this.url("/" + this.orgId + "/system/user/set/kickout/" + item.userId + "/" + val), null, () => {
        item.loading = false;
        this.toast("操作成功");
        this.doQuery(this.pagination.page);
      }, () => {
        this.doQuery(this.pagination.page);
      });
    },

    /*
     * 抽屉窗口的显示标题
     */
    showDrawerTitle(userName, realName) {
      return userName + (realName? (' (' + realName + ')') : '');
    }
  }
};

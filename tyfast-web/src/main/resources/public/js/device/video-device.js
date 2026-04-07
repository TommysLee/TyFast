// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "视频设置",
      // 查询条件
      orgId: null,
      selectedOrgIds: [],
      param: {
      },
      // 数据表格
      datatable: {
        headers: [
          { title: '#', value:'index', align:"center", width: 60},
          { title: '机构', value:'orgName'},
          { title: '设备名称', value:'deviceName'},
          { title: '设备序列号', value:'serialNum'},
          { title: '通道号', value:'channel', align:"center"},
          { title: '排序号', value:'orderNum', align:"center"},
          { title: '播放地址', value:'playurl', align:"center"},
          { title: '状态', value:'enable', align:"center"},
          { title: '创建时间', value:'createTime', align:"center", width:180},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        total: 0
      },
      // 表单数据
      formData: {
        deviceId: null,
        deviceName: null,
        serialNum: null,
        channel: 1,
        hlsLiveHdUrl: null,
        hlsLiveUrl: null,
        monitorHdUrl: null,
        monitorUrl: null,
        orderNum: 1
      },
      // 播放地址URL对象
      playUrl: {
        title: '',
        subTitle: '',
        url: null,
        hdUrl: null
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: '',

      playurlDialog: false
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
      if (!this.$refs['updatePermis'] && !this.$refs['delPermis']) {
        this.datatable.headers.remove(9);
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

        doAjax(this.url("/" + this.orgId + "/device/video/list"), this.param, (result) => {
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
      this.formData.deviceId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/" + this.orgId + "/device/video/single/" + id), null, (result) => {
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
      this.method = this.formData.deviceId? "update" : "save";
      doAjaxPost(this.url("/" + this.orgId + "/device/video/" + this.method), this.formData, (result) => {
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
    doDelete(deviceId) {
      this.method = "del";
      doAjaxGet(this.url("/" + this.orgId + "/device/video/del/" + deviceId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 打开或关闭PlayURL窗口
     */
    togglePlayURLDialog(item, flag) {
      this.playurlDialog = !this.playurlDialog;
      switch (flag) {
        case 1:
          this.playUrl = {
            title: '直播地址',
            subTitle: '直播HLS地址',
            url: item.hlsLiveUrl,
            hdUrl: item.hlsLiveHdUrl
          }
          break;
        case 2:
          this.playUrl = {
            title: '监控地址',
            subTitle: '监控地址',
            url: item.monitorUrl,
            hdUrl: item.monitorHdUrl
          }
          break;
      }
    },

    /*
     * 更改设备状态
     */
    changeStatus(val, item) {
      this.method = 'status';
      item.loading = true;
      doAjaxGet(this.url("/" + this.orgId + "/device/video/set/status/" + item.deviceId  + "/" + val), null, () => {
        item.loading = false;
        this.toast("操作成功");
        this.doQuery(this.pagination.page);
      }, () => {
        this.doQuery(this.pagination.page);
      })
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');
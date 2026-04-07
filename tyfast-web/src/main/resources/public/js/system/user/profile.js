const homeName = '默认首页';
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "个人设置",
      tab: null,
      deactiveMenu: true,
      formData: {
        password: null,
        newPassword: null
      },
      confirmPassword: null,

      homeName,
      homeAction: null,
      parentMenuId: 0,
      myMenuList: [],
      selectedMenuId: null,

      // 抽屉窗口
      winDrawer: false,
      winDrawerWidth: 800,

      // 树菜单配置项
      openItems: [],
      selectItems: [],

      // Confirm窗口
      csheet: false,

      // 第三方账号绑定状态
      wxbind: -1,
      unionId: null
    }
  },

  computed: {
    disableClear() {
      return !('string' === typeof(this.homeAction) && this.homeAction.length > 0);
    },
    tabWidth() {
      switch (this.$vuetify.display.name) {
        case 'xs': return null
        case 'sm': return '150px'
        default: return '200px'
      }
    }
  },

  watch: {
    homeAction(val) {
      this.selectedMenuId = val;
    },
    selectItems(val) {
      this.selectedMenuId = val.length > 0? val[0] : null;
    },
    tab(val) {
      switch (val) {
        case '1':
          this.resetForm();
          break;
        case '2':
          this.queryHomeSettings();
          break;
        case '3':
          this.queryBindState();
          break;
      }
    }
  },

  methods: {
    /*
     * 修改密码
     */
    doSubmit() {
      this.posting = true;
      doAjax(this.url("/system/user/password/update"), this.formData, data => {
        if (data.state) {
          this.toast("密码修改成功");
          this.resetForm();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 清除默认首页
     */
    clearHomeSettings() {
      doAjaxGet(this.url("/system/user/profile/myhome/clear"), null, () => {
        this.toast("设置清除成功");
        this.homeName = homeName;
        this.homeAction = null;
      });
    },

    /*
     * 查询默认首页
     */
    queryHomeSettings() {
      this.loading = true;
      doAjaxGet(this.url("/system/user/profile/myhome"), null, (data) => {
        let myHome = data.data;
        if (myHome) {
          this.homeName = myHome.menuAlias || myHome.menuName || homeName;
          this.homeAction = myHome.menuId;
          this.parentMenuId = myHome.parentId || 0;
        } else {
          this.homeName = homeName;
          this.homeAction = null;
          this.parentMenuId = 0;
        }
      });
    },

    /*
     * 更新默认首页
     */
    updateHomeSettings() {
      this.posting = true;
      if (this.selectedMenuId) {
        doAjaxGet(this.url("/system/user/profile/myhome/update/" + this.selectedMenuId), null, () => {
          this.toast("默认首页设置成功");
          this.closeWinDrawer();
          this.queryHomeSettings();
        });
      }
    },

    /*
     * 打开抽屉窗口
     */
    openWinDrawer() {
      this.winDrawer = true;
      this.loading = true;
      this.reloadNavMenus((navMenus) => {
        this.myMenuList = navMenus;

        // 选中当前的默认首页
        if (this.homeAction) {
          this.selectItems = [this.homeAction];
          this.openItems = [this.parentMenuId];
        } else {
          this.selectItems = [];
          this.openItems = [];
        }
      });
    },

    /*
     * 关闭抽屉窗口
     */
    closeWinDrawer() {
      this.winDrawer = false;
    },

    /*
     * 查询绑定状态
     */
    queryBindState() {
      this.loading = true;
      doAjaxGet(this.url("/user/bind/state/weixin"), null, (result) => {
        this.wxbind = result.data;
      });
    },

    /*
     * 打开微信绑定页
     */
    openWxbind() {
      doAjaxGet(this.url("/open/oauth2/weixin/bind/connect"), null, (result) => {
        window.open(result.data,'', 'left=240,top=200,width=600,height=600');
      })
    },

    /*
     * 保存微信绑定关系
     */
    saveWxbind() {
      this.csheet = false;
      this.loading = true;
      doAjaxPost(this.url("/user/bind/save/weixin"), {unionId:this.unionId}, (r) => {
        if (r.state) {
          this.toast("微信绑定成功");
          this.queryBindState();
        } else {
          this.toast(r.message, 'warning');
        }
      })
    },

    /*
     * 清除微信绑定关系
     */
    clearWxbind() {
      doAjaxPost(this.url("/user/bind/clear/weixin"), null, (result) => {
        if (result.state) {
          this.unionId = null;
          this.toast("微信绑定已解除");
          this.queryBindState();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');

/*
 * 微信绑定子页面的回调接口
 */
window.callWxbind = function (unionId, count) {
  console.log("Wx Bind CK: " + count);
  appInstance.unionId = unionId;
  if (count > 0) {
    appInstance.csheet = true;
  } else {
    appInstance.saveWxbind();
  }
}
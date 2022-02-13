/**
 * Skipped minification because the original files appears to be already minified.
 * Original file: /npm/vuetify-message-snackbar@0.2.7/dist/vuetify-message-snackbar.js
 *
 * Do NOT use SRI with dynamically generated files! More information: https://www.jsdelivr.com/using-sri-with-dynamic-files
 *
 * https://thinkupp.github.io/demo/#/vuetify/message
 * https://github.com/thinkupp/vuetify-message-snackbar
 */
!function(t,o){"object"==typeof exports&&"object"==typeof module?module.exports=o(require("vue"),require("vuetify")):"function"==typeof define&&define.amd?define(["vue","vuetify"],o):"object"==typeof exports?exports.VuetifyMessageSnackbar=o(require("vue"),require("vuetify")):t.VuetifyMessageSnackbar=o(t.Vue,t.Vuetify)}(window,(function(t,o){return function(t){var o={};function e(n){if(o[n])return o[n].exports;var i=o[n]={i:n,l:!1,exports:{}};return t[n].call(i.exports,i,i.exports,e),i.l=!0,i.exports}return e.m=t,e.c=o,e.d=function(t,o,n){e.o(t,o)||Object.defineProperty(t,o,{enumerable:!0,get:n})},e.r=function(t){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},e.t=function(t,o){if(1&o&&(t=e(t)),8&o)return t;if(4&o&&"object"==typeof t&&t&&t.__esModule)return t;var n=Object.create(null);if(e.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:t}),2&o&&"string"!=typeof t)for(var i in t)e.d(n,i,function(o){return t[o]}.bind(null,i));return n},e.n=function(t){var o=t&&t.__esModule?function(){return t.default}:function(){return t};return e.d(o,"a",o),o},e.o=function(t,o){return Object.prototype.hasOwnProperty.call(t,o)},e.p="",e(e.s=2)}([function(o,e){o.exports=t},function(t,e){t.exports=o},function(t,o,e){"use strict";e.r(o),e.d(o,"Notify",(function(){return P})),e.d(o,"setVuetifyInstance",(function(){return y}));var n=function(){return(n=Object.assign||function(t){for(var o,e=1,n=arguments.length;e<n;e++)for(var i in o=arguments[e])Object.prototype.hasOwnProperty.call(o,i)&&(t[i]=o[i]);return t}).apply(this,arguments)};Object.create;Object.create;var i=e(0),r=e.n(i),s=e(1),u=e.n(s),p={success:"mdi-checkbox-marked-circle",warning:"mdi-alert-circle",info:"mdi-information",error:"mdi-close-circle"},a=function(){this.top=!0,this.bottom=!1,this.left=!1,this.right=!1,this.timeout=3e3,this.autoRemove=!0,this.closeButtonContent="",this.offsetTop=10};function c(t){if(!t||"object"!=typeof t)return!1;var o,e,n=Object.getPrototypeOf(t);return!(!n||!n.constructor)&&("vnode"===n.constructor.name.toLowerCase()&&(o=t,e="isRootInsert",Object.hasOwnProperty.call(o,e)))}var f,h,l=(f=["fab","fade","scale"],["scroll","slide"].forEach((function(t){f.push(t+"-x"),f.push(t+"-y"),f.push(t+"-x-reverse"),f.push(t+"-y-reverse")})),f.map((function(t){return t+"-transition"})));function m(t,o){return h||(h=t||new u.a(o||{}))}function y(t){return t&&t.constructor&&"Vuetify"===t.constructor.name?(h=t,!0):(console.warn("[vuetify-message-snackbar]: invalid vuetify instance"),!1)}var d=document.documentElement,g=function(){function t(t,o){this.element=d,this.isCloseAll=!1,this.instance=t,this.messageConfig=t.messageConfig,this.appendTo=o,this.setOffsetTop(),this.mount()}return t.closeAll=function(){Object.keys(t.messageQueue).forEach((function(o){t.messageQueue[o].forEach((function(t){t.close(!0)}))}))},t.prototype.destroy=function(){for(var t=!1,o=0,e=this.getQueue(),n=0;n<e.length;n++){var i=e[n].$el,r=i.firstElementChild;if(e[n]!==this.instance){if(t){var s=o;this.messageInTop()||(s=-o),i.style.top=s+"px"}o+=r.offsetHeight+e[n].messageConfig.offsetTop}else{var u=e[n].isCloseAll;if(e.splice(n,1),this.remove(),u)return;t=!0,n--}}},t.prototype.mount=function(){var t=this.instance;t.$mount();var o=0,e=this.getQueue();e.forEach((function(t){o+=t.$el.firstElementChild.offsetHeight+t.messageConfig.offsetTop})),this.messageInTop()||(o=-o),t.$el.style.top=o+"px",this.append(),e.push(t)},t.prototype.append=function(){this.setAppendToElement(),this.element.appendChild(this.instance.$el)},t.prototype.remove=function(){var t=this;this.messageConfig.autoRemove&&setTimeout((function(){t.element.removeChild(t.instance.$el)}),500)},t.prototype.getPosition=function(){var t=this.messageConfig,o=t.top,e=t.left,n=o?"top":"bottom";return t.right?n+="Right":e&&(n+="Left"),n},t.prototype.getQueue=function(){var o=this.getPosition();return t.messageQueue[o]},t.prototype.setOffsetTop=function(){var o=this.messageConfig.offsetTop,e=o;null==o||isNaN(Number(o))?e=t.DEFAULT_OFFSET_TOP:"number"!=typeof o&&(e=Number(o)),this.messageConfig.offsetTop=e},t.prototype.messageInTop=function(){return this.messageConfig.top},t.prototype.setAppendToElement=function(){if(d===document.documentElement||d===document.body||!document.documentElement.contains(d)){var t,o=this.appendTo;if(o instanceof Element)t=o;else if("string"==typeof o){var e=document.querySelector(o);e&&(t=e)}else t=document.querySelector("#app.v-application")||document.body;t&&(this.element=d=t)}},t.DEFAULT_OFFSET_TOP=20,t.messageQueue={top:[],topLeft:[],topRight:[],bottom:[],bottomLeft:[],bottomRight:[]},t}();function v(t,o){return r.a.extend({data:function(){return this.messageConfig=t,{value:!1,isCloseAll:!1}},vuetify:m(),methods:{close:function(t){this.value=!1,this.isCloseAll="boolean"==typeof t&&t},getChildren:function(o){var e=t.closeButtonContent,n=this.getMessage(o);return e&&n.push(this.getCloseButton(o,e)),n},getMessage:function(e){var n=t.message;if(c(n))return[n];var i=function(t,o,e){return o||(""!==o?(e||{})[t=(t||"").toLowerCase()]||p[t]:void 0)}(t.color,t.messageIcon,o);return i?(c(i)||(i=e("v-icon",{props:{left:!0}},[i])),[i,n]):[n]},getCloseButton:function(t,o){return c(o)?t("template",{slot:"action"},[o]):t("v-btn",{props:{dark:!0,text:!0},on:{click:this.close},slot:"action"},o)},getClassName:function(){var o=t.class,e={};return"string"==typeof o?o=(o=o.trim())?[o]:[]:o instanceof Array||(o=["margin-top-animation"]),o.forEach((function(t){e[t]=!0})),e}},watch:{value:function(){this.value||this.$emit("closed",this.isCloseAll)}},mounted:function(){this.value=!0},render:function(o){var e,i,r=this;return o("v-snackbar",{props:n(n({},(e=t,i=Object.assign({},e),["autoRemove","closeButtonContent","offsetTop","appendTo","message","class","autoTransitionSetting"].forEach((function(t){void 0!==i[t]&&delete i[t]})),i)),{value:r.value}),on:{input:function(t){r.value=t,r.$emit("input",t)}},class:r.getClassName()},r.getChildren(o))}})}function b(t,o,e){var n=new(v(t,e)),i=new g(n,o);return n.$on("closed",(function(){i.destroy()})),n}var T,C,x=function(){function t(t){this.option={},this.transitionIsAutoSetting=!1,this.pool={},void 0===t&&(t=!0),this.autoTransitionSetting=!!t}return t.prototype.top=function(){return this.initDirection(),this.option.top=!0,this.setTransitionByPosition("top"),this},t.prototype.topLeft=function(){return this.initDirection(),this.option.top=!0,this.option.left=!0,this.setTransitionByPosition("topLeft"),this},t.prototype.topRight=function(){return this.initDirection(),this.option.top=!0,this.option.right=!0,this.setTransitionByPosition("topRight"),this},t.prototype.bottom=function(){return this.initDirection(),this.option.bottom=!0,this.setTransitionByPosition("bottom"),this},t.prototype.bottomLeft=function(){return this.initDirection(),this.option.bottom=!0,this.option.left=!0,this.setTransitionByPosition("bottomLeft"),this},t.prototype.bottomRight=function(){return this.initDirection(),this.option.bottom=!0,this.option.right=!0,this.setTransitionByPosition("bottomRight"),this},t.prototype.absolute=function(t){return this.setBoolean("absolute",t)},t.prototype.app=function(t){return this.setBoolean("app",t)},t.prototype.centered=function(t){return this.setBoolean("centered",t)},t.prototype.dark=function(t){return this.setBoolean("dark",t)},t.prototype.light=function(t){return this.setBoolean("light",t)},t.prototype["multi-line"]=function(t){return this.multiLine(t)},t.prototype.multiLine=function(t){return this.setBoolean("multi-line",t)},t.prototype.outlined=function(t){return this.setBoolean("outlined",t)},t.prototype.shaped=function(t){return this.setBoolean("shaped",t)},t.prototype.text=function(t){return this.setBoolean("text",t)},t.prototype.tile=function(t){return this.setBoolean("tile",t)},t.prototype.vertical=function(t){return this.setBoolean("vertical",t)},t.prototype.autoRemove=function(t){return this.setBoolean("autoRemove",t)},t.prototype.color=function(t){return this.option.color=t,this},t.prototype["content-class"]=function(t){return this.contentClass(t)},t.prototype.contentClass=function(t){return this.option["content-class"]=t,this},t.prototype.elevation=function(t){return this.option.elevation=t,this},t.prototype.height=function(t){return this.option.height=t,this},t.prototype.maxHeight=function(t){return this.option["max-height"]=t,this},t.prototype["max-height"]=function(t){return this.maxHeight(t)},t.prototype.maxWidth=function(t){return this.option["max-width"]=t,this},t.prototype["max-width"]=function(t){return this.maxWidth(t)},t.prototype.rounded=function(t){return this.option.rounded=t,this},t.prototype.tag=function(t){return this.option.tag=t,this},t.prototype.timeout=function(t){return this.option.timeout=t,this},t.prototype.transition=function(t){var o,e;return void 0===t&&(e=l.length,void 0===o?o=Math.floor(Math.random()*e):o<0?o=0:o>=e&&(o=e-1),t=l[o]),this.transitionIsAutoSetting=!1,this.option.transition=t,this},t.prototype.width=function(t){return this.option.width=t,this},t.prototype.offsetTop=function(t){return this.option.offsetTop=t,this},t.prototype.closeButtonContent=function(t){return this.option.closeButtonContent=t,this},t.prototype.getOption=function(){var t=this.option;return this.option={},t},t.prototype.save=function(t){var o=this.getOption();return this.pool[t]=o,this.option=o,this},t.prototype.read=function(t){return this.option=this.pool[t],this},t.prototype.messageIcon=function(t){return this.option.messageIcon=t||"",this},t.prototype.initDirection=function(){var t=this.option;t.top=!1,t.left=!1,t.right=!1,t.bottom=!1},t.prototype.setBoolean=function(t,o){return void 0===o&&(o=!0),this.option[t]=o,this},t.prototype.setTransitionByPosition=function(t){if((!this.option.transition||this.transitionIsAutoSetting)&&this.autoTransitionSetting){var o={top:"scroll-y",topLeft:"slide-x",topRight:"slide-x-reverse",bottom:"scroll-y-reverse",bottomLeft:"slide-x",bottomRight:"slide-x-reverse"};this.transitionIsAutoSetting=!0,this.option.transition=o[t]+"-transition"}},t}(),O={},B=["success","info","warning","error","show"];function w(){Object.setPrototypeOf(C,new x(O.autoTransitionSetting))}var P=C||(C=function(t,o){var e;T||((e=document.createElement("style")).innerText=".margin-top-animation { transition: top .15s linear }",document.head.appendChild(e),T=!0),o&&(B.includes(o)&&"show"!==o||(o=void 0));var i=n(n(n(n({},new a),O),C.getOption()),function(t,o){return t?(("string"==typeof t||c(t))&&(t={message:t}),o&&!t.color&&(t.color=o),t):{}}(t,o));return{close:b(i,O.appendTo,null==O?void 0:O.presetIcon).close,again:function(){return{close:b(i,O.appendTo,null==O?void 0:O.presetIcon).close}}}},w(),B.forEach((function(t){C[t]=function(o){return C(o,t)}})),C.closeAll=g.closeAll,C);o.default={install:function(t,o){var e;O=e=o||{},m(e.vuetifyInstance,e.vuetifyPreset),w(),t.prototype.$message=P}}}])}));
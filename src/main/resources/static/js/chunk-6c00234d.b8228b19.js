(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-6c00234d"],{"0393":function(e,t,n){"use strict";var s=n("5530"),a=(n("0481"),n("210b"),n("604c")),i=n("d9bd");t["a"]=a["a"].extend({name:"v-expansion-panels",provide:function(){return{expansionPanels:this}},props:{accordion:Boolean,disabled:Boolean,flat:Boolean,hover:Boolean,focusable:Boolean,inset:Boolean,popout:Boolean,readonly:Boolean,tile:Boolean},computed:{classes:function(){return Object(s["a"])(Object(s["a"])({},a["a"].options.computed.classes.call(this)),{},{"v-expansion-panels":!0,"v-expansion-panels--accordion":this.accordion,"v-expansion-panels--flat":this.flat,"v-expansion-panels--hover":this.hover,"v-expansion-panels--focusable":this.focusable,"v-expansion-panels--inset":this.inset,"v-expansion-panels--popout":this.popout,"v-expansion-panels--tile":this.tile})}},created:function(){this.$attrs.hasOwnProperty("expand")&&Object(i["a"])("expand","multiple",this),Array.isArray(this.value)&&this.value.length>0&&"boolean"===typeof this.value[0]&&Object(i["a"])(':value="[true, false, true]"',':value="[0, 2]"',this)},methods:{updateItem:function(e,t){var n=this.getValue(e,t),s=this.getValue(e,t+1);e.isActive=this.toggleMethod(n),e.nextIsActive=this.toggleMethod(s)}}})},"210b":function(e,t,n){},"49e2":function(e,t,n){"use strict";var s=n("0789"),a=n("9d65"),i=n("a9ad"),o=n("3206"),r=n("80d2"),l=n("58df"),c=Object(l["a"])(a["a"],i["a"],Object(o["a"])("expansionPanel","v-expansion-panel-content","v-expansion-panel"));t["a"]=c.extend().extend({name:"v-expansion-panel-content",data:function(){return{isActive:!1}},computed:{parentIsActive:function(){return this.expansionPanel.isActive}},watch:{parentIsActive:{immediate:!0,handler:function(e,t){var n=this;e&&(this.isBooted=!0),null==t?this.isActive=e:this.$nextTick((function(){return n.isActive=e}))}}},created:function(){this.expansionPanel.registerContent(this)},beforeDestroy:function(){this.expansionPanel.unregisterContent()},render:function(e){var t=this;return e(s["a"],this.showLazyContent((function(){return[e("div",t.setBackgroundColor(t.color,{staticClass:"v-expansion-panel-content",directives:[{name:"show",value:t.isActive}]}),[e("div",{class:"v-expansion-panel-content__wrap"},Object(r["s"])(t))])]})))}})},"9c75":function(e,t,n){"use strict";n.r(t);var s=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("v-container",{attrs:{"grid-list-xs":""}},[n("SnackBar",{ref:"sb"}),n("v-card",{staticClass:"pa-5"},[n("div",{staticClass:"tw-flex tw-justify-between tw-items-center tw-p-2"},[n("div",{staticClass:"tw-text-2xl"},[e._v(" ID:"+e._s(this.$route.params.resultId)+"的结果 ")]),n("div",[n("v-btn",{attrs:{color:"primary"},on:{click:function(t){return e.back()}}},[e._v("返回")])],1)]),n("div",{staticClass:"tw-overflow-y-scroll",staticStyle:{height:"400px"}},[n("v-expansion-panels",{attrs:{flat:""}},e._l(e.results,(function(t,s){return n("v-expansion-panel",{key:s,staticClass:"grey lighten-4"},[n("v-expansion-panel-header",[n("div",{staticClass:"tw-flex tw-justify-around"},[n("div",[e._v(e._s(t.id))]),n("div",[e._v(e._s(t.timeCost)+"ms")]),n("div",[e._v(e._s(t.memoryCost)+"MB")]),n("div",{staticClass:"tw-px-2 tw-py-1 tw-text-white tw-rounded-md tw-w-min",class:e.colorMap[t.color]},[e._v(" "+e._s(t.code)+" ")])])]),n("v-expansion-panel-content",[n("div",{staticClass:"tw-p-2 tw-overflow-auto tw-rounded-md",class:t.correct?"tw-bg-gray-100 ":"tw-bg-red-100 tw-text-red-600"},[n("pre",{staticClass:"tw-font-mono tw-text-xs"},[e._v(e._s(t.message))])])])],1)})),1)],1),n("div",{staticClass:"tw-mt-5"},[n("vue-codeditor",{staticStyle:{"font-size":"16px","min-height":"600px"},attrs:{theme:"katzenmilch",mode:e.select,readonly:"true"},model:{value:e.code,callback:function(t){e.code=t},expression:"code"}})],1)])],1)},a=[],i=n("4c05"),o=n("4ec3"),r={components:{SnackBar:i["a"]},mounted:function(){this.getDataFromApi()},computed:{answerClass:function(){return"tw-bg-red-100 tw-p-2 tw-h-48 tw-overflow-auto tw-rounded-md"}},data:function(){return{ifsubmit:!1,ifHaveAnswer:!1,testLoader:!1,submitLoader:!1,resultMap:{},finialResult:"AC",correctNum:0,totalNum:0,results:[{id:"",total:"",isCorrect:"",timeCost:"",memoryCost:"",code:"",name:"",message:"",color:""}],result:null,language:null,testCase:"",isReturn:!1,colorMap:{RED:"tw-bg-red-600",ORANGE:"tw-bg-yellow-600",YELLOW:"tw-bg-yellow-400",GREEN:"tw-bg-green-600",BLUE:"tw-bg-blue-600",PURPLE:"tw-bg-purple-600",GRAY:"tw-bg-gray-600",PINK:"tw-bg-pink-600"},code:"",select:"java",tab:null,title:["测试","提交"],selections:["java","python"],standardResult:{correct:!0,timeCost:"",memoryCost:"",code:"",name:"",message:"",color:"GRAY"},userResult:{correct:!0,timeCost:"",memoryCost:"",code:"",name:"",message:"",color:""},res_code_map_one:{0:"成功",1:"停止访问","-1":"问题不存在","-2":"没有标答","-3":"测试机错误"},submitId:null}},methods:{getDataFromApi:function(){var e=this;o["a"].submitFactory.getsubmitAnswer(this.$route.params.resultId).then((function(t){e.results=t.content.records,e.code=t.content.code,console.log(t)}))},back:function(){this.$router.go(-1)}}},l=r,c=n("2877"),d=n("6544"),u=n.n(d),p=n("8336"),h=n("b0af"),v=n("a523"),x=n("cd55"),f=n("49e2"),m=n("c865"),b=n("0393"),w=Object(c["a"])(l,s,a,!1,null,null,null);t["default"]=w.exports;u()(w,{VBtn:p["a"],VCard:h["a"],VContainer:v["a"],VExpansionPanel:x["a"],VExpansionPanelContent:f["a"],VExpansionPanelHeader:m["a"],VExpansionPanels:b["a"]})},c865:function(e,t,n){"use strict";var s=n("5530"),a=n("0789"),i=n("9d26"),o=n("a9ad"),r=n("3206"),l=n("5607"),c=n("80d2"),d=n("58df"),u=Object(d["a"])(o["a"],Object(r["a"])("expansionPanel","v-expansion-panel-header","v-expansion-panel"));t["a"]=u.extend().extend({name:"v-expansion-panel-header",directives:{ripple:l["a"]},props:{disableIconRotate:Boolean,expandIcon:{type:String,default:"$expand"},hideActions:Boolean,ripple:{type:[Boolean,Object],default:!1}},data:function(){return{hasMousedown:!1}},computed:{classes:function(){return{"v-expansion-panel-header--active":this.isActive,"v-expansion-panel-header--mousedown":this.hasMousedown}},isActive:function(){return this.expansionPanel.isActive},isDisabled:function(){return this.expansionPanel.isDisabled},isReadonly:function(){return this.expansionPanel.isReadonly}},created:function(){this.expansionPanel.registerHeader(this)},beforeDestroy:function(){this.expansionPanel.unregisterHeader()},methods:{onClick:function(e){this.$emit("click",e)},genIcon:function(){var e=Object(c["s"])(this,"actions")||[this.$createElement(i["a"],this.expandIcon)];return this.$createElement(a["c"],[this.$createElement("div",{staticClass:"v-expansion-panel-header__icon",class:{"v-expansion-panel-header__icon--disable-rotate":this.disableIconRotate},directives:[{name:"show",value:!this.isDisabled}]},e)])}},render:function(e){var t=this;return e("button",this.setBackgroundColor(this.color,{staticClass:"v-expansion-panel-header",class:this.classes,attrs:{tabindex:this.isDisabled?-1:null,type:"button","aria-expanded":this.isActive},directives:[{name:"ripple",value:this.ripple}],on:Object(s["a"])(Object(s["a"])({},this.$listeners),{},{click:this.onClick,mousedown:function(){return t.hasMousedown=!0},mouseup:function(){return t.hasMousedown=!1}})}),[Object(c["s"])(this,"default",{open:this.isActive},!0),this.hideActions||this.genIcon()])}})},cd55:function(e,t,n){"use strict";var s=n("5530"),a=n("4e82"),i=n("3206"),o=n("80d2"),r=n("58df");t["a"]=Object(r["a"])(Object(a["a"])("expansionPanels","v-expansion-panel","v-expansion-panels"),Object(i["b"])("expansionPanel",!0)).extend({name:"v-expansion-panel",props:{disabled:Boolean,readonly:Boolean},data:function(){return{content:null,header:null,nextIsActive:!1}},computed:{classes:function(){return Object(s["a"])({"v-expansion-panel--active":this.isActive,"v-expansion-panel--next-active":this.nextIsActive,"v-expansion-panel--disabled":this.isDisabled},this.groupClasses)},isDisabled:function(){return this.expansionPanels.disabled||this.disabled},isReadonly:function(){return this.expansionPanels.readonly||this.readonly}},methods:{registerContent:function(e){this.content=e},unregisterContent:function(){this.content=null},registerHeader:function(e){this.header=e,e.$on("click",this.onClick)},unregisterHeader:function(){this.header=null},onClick:function(e){e.detail&&this.header.$el.blur(),this.$emit("click",e),this.isReadonly||this.isDisabled||this.toggle()},toggle:function(){var e=this;this.$nextTick((function(){return e.$emit("change")}))}},render:function(e){return e("div",{staticClass:"v-expansion-panel",class:this.classes,attrs:{"aria-expanded":String(this.isActive)}},Object(o["s"])(this))}})}}]);
//# sourceMappingURL=chunk-6c00234d.b8228b19.js.map
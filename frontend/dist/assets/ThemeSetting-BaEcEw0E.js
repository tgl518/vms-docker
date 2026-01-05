import{f as O,z as S,A as g,B as l,C as w,M as B,J as a,N as b,F as C,r as I,T as N,O as E,P as M,Q as P,I as x,E as y,_ as z,R as j,D as s,S as R,G as A,U as V,K as F,V as T,j as q,h as k,w as D,v as U,W,H,X as L,Y,L as G,Z as X}from"./index-B-Gu03yZ.js";import{N as J}from"./Dropdown-02AqhRlH.js";import"./UserAvatar-2hx7tV6f.js";import{_ as K}from"./_plugin-vue_export-helper-DlAUqK2U.js";import{_ as Q,a as Z}from"./Tabs-CySlKHww.js";import{_ as tt}from"./ColorPicker-DJHWI4-E.js";var nt=void 0;const et=(t,e)=>{let n=null,i=!0;return function(){if(!i)return;i=!1;for(var o=arguments.length,d=new Array(o),p=0;p<o;p++)d[p]=arguments[p];let m=d;n&&clearTimeout(n),n=setTimeout(()=>{i=!0,t.apply(nt,m)},e)}};var _=O({name:"Vue3IntroStep",props:{show:{type:Boolean,required:!0},config:{type:Object,required:!0}},emits:["update:show"],data(){return{originalBox:{left:250,top:250,width:200,height:100},tipBoxPosition:"bottom",currentIndex:0}},watch:{config:{deep:!0,handler(){this.currentIndex=0},immediate:!0},show(t){t?this.setBoxInfo():document.body.style.overflow="auto"}},computed:{tipBoxStyle(){if(this.tipBoxPosition==="right")return{left:`${this.originalBox.left+this.originalBox.width}px`,top:`${this.originalBox.top}px`};if(this.tipBoxPosition==="left")return{right:`${window.innerWidth-this.originalBox.left}px`,top:`${this.originalBox.top}px`};if(this.tipBoxPosition==="top")return{left:`${this.originalBox.left}px`,bottom:`${window.innerHeight-this.originalBox.top}px`};if(this.tipBoxPosition==="bottom")return{left:`${this.originalBox.left>window.innerWidth-300?window.innerWidth-300:this.originalBox.left}px`,top:`${this.originalBox.top+this.originalBox.height}px`}}},created(){this.init()},mounted(){window.onresize=et(()=>{this.show&&this.setBoxInfo()},100)},beforeUnmount(){window.onresize=null},methods:{async prev(){let t=!0;if(this.config.tips[this.currentIndex]&&this.config.tips[this.currentIndex].onPrev&&(t=await this.config.tips[this.currentIndex].onPrev()),!t)throw new Error("onPrev 需要 Promise.resolve(true) 才可以继续往下走");this.setBoxInfo(this.currentIndex-1)},async next(){let t=!0;if(this.config.tips[this.currentIndex]&&this.config.tips[this.currentIndex].onNext&&(t=await this.config.tips[this.currentIndex].onNext()),!t)throw new Error("onNext 需要 Promise.resolve(true) 才可以继续往下走");this.setBoxInfo(this.currentIndex+1)},done(){this.$emit("update:show",!1)},async setBoxInfo(t){try{t===void 0&&(t=this.currentIndex),this.show&&(document.body.style.overflow="hidden");let e=this.config.tips[t].el,n=document.querySelector(e);if(!n)throw new Error("没有找到相应的元素");let i=n.getBoundingClientRect();this.originalBox={left:i.left,top:i.top,width:i.width,height:i.height},this.tipBoxPosition=this.config.tips[t].tipPosition,this.currentIndex=t}catch(e){throw new Error(e.message)}},init(){const{tips:t}=this.config;let e=null;if(t&&Array.isArray(t))if(t.length>0){this.currentIndex=0;try{let n=document.querySelector(t[0].el);e=setInterval(()=>{n=document.querySelector(t[0].el),n&&(this.setBoxInfo(0),clearInterval(e))},0)}catch(n){throw new Error(n.message)}}else throw new Error("tips数组不能为空");else throw new Error("config中的tips不存在或者不是数组")}}});const ot=t=>(E("data-v-5d3b253c"),t=t(),M(),t),it={key:0,id:"intro_box"},rt=ot(()=>a("div",{class:"round round-flicker"},null,-1)),st=[rt],at={class:"tip-content"},lt={class:"action",style:{justifyContent:"center"}};function ct(t,e,n,i,o,d){return g(),S(N,{name:"custom-classes-transition","enter-active-class":"animate__animated animate__fadeIn animate__faster","leave-active-class":"animate__animated animate__fadeOut animate__faster"},{default:l(()=>[t.show?(g(),w("div",it,[a("div",{class:"top",style:b({height:`${t.originalBox.top}px`,backgroundColor:`rgba(0, 0, 0, ${t.config.backgroundOpacity?t.config.backgroundOpacity:.9})`})},null,4),a("div",{class:"content",style:b({height:`${t.originalBox.height}px`})},[a("div",{class:"left",style:b({top:`${t.originalBox.top}px`,width:`${t.originalBox.left}px`,height:`${t.originalBox.height}px`,backgroundColor:`rgba(0, 0, 0, ${t.config.backgroundOpacity?t.config.backgroundOpacity:.9})`})},null,4),a("div",{class:"original-box",style:b({top:`${t.originalBox.top}px`,left:`${t.originalBox.left}px`,width:`${t.originalBox.width}px`,height:`${t.originalBox.height}px`})},st,4),a("div",{class:"tip-box",style:b(t.tipBoxStyle)},[a("div",at,[t.config.tips[t.currentIndex].title?(g(),w("div",{key:0,class:"title",style:b({textAlign:t.config.titleStyle&&t.config.titleStyle.textAlign?t.config.titleStyle.textAlign:"center",fontSize:t.config.titleStyle&&t.config.titleStyle.fontSize?t.config.titleStyle.fontSize:"19px"})},C(t.config.tips[t.currentIndex].title),5)):B("",!0),a("div",{class:"content",style:b({textAlign:t.config.contentStyle&&t.config.contentStyle.textAlign?t.config.contentStyle.textAlign:"center",fontSize:t.config.contentStyle&&t.config.contentStyle.fontSize?t.config.contentStyle.fontSize:"15px"})},C(t.config.tips[t.currentIndex].content),5),a("div",lt,[t.currentIndex!==0?I(t.$slots,"prev",{key:0,index:t.currentIndex,tipItem:t.config.tips[t.currentIndex]},()=>[a("div",{class:"item prev",onClick:e[0]||(e[0]=function(){return t.prev&&t.prev(...arguments)})},"上一步")]):B("",!0),t.currentIndex!==t.config.tips.length-1?I(t.$slots,"next",{key:1,index:t.currentIndex,tipItem:t.config.tips[t.currentIndex]},()=>[a("div",{class:"item next",onClick:e[1]||(e[1]=function(){return t.next&&t.next(...arguments)})},"下一步")]):B("",!0),t.currentIndex===t.config.tips.length-1?I(t.$slots,"done",{key:2,index:t.currentIndex,tipItem:t.config.tips[t.currentIndex]},()=>[a("div",{class:"item done",onClick:e[2]||(e[2]=function(){return t.done&&t.done(...arguments)})},"完成")]):I(t.$slots,"skip",{key:3,index:t.currentIndex,tipItem:t.config.tips[t.currentIndex]},()=>[a("div",{class:"item skip",onClick:e[3]||(e[3]=function(){return t.done&&t.done(...arguments)})},"跳过")])])])],4),a("div",{class:"right",style:b({top:`${t.originalBox.top}px`,left:`${t.originalBox.left+t.originalBox.width}px`,width:`calc(100% - ${t.originalBox.left+t.originalBox.width}px)`,height:`${t.originalBox.height}px`,backgroundColor:`rgba(0, 0, 0, ${t.config.backgroundOpacity?t.config.backgroundOpacity:.9})`}),ref:"tip_box"},null,4)],4),a("div",{class:"bottom",style:b({height:`calc(100% - ${t.originalBox.top}px - ${t.originalBox.height}px)`,backgroundColor:`rgba(0, 0, 0, ${t.config.backgroundOpacity?t.config.backgroundOpacity:.9})`})},null,4)])):B("",!0)]),_:3})}function dt(t,e){e===void 0&&(e={});var n=e.insertAt;if(!(typeof document>"u")){var i=document.head||document.getElementsByTagName("head")[0],o=document.createElement("style");o.type="text/css",n==="top"&&i.firstChild?i.insertBefore(o,i.firstChild):i.appendChild(o),o.styleSheet?o.styleSheet.cssText=t:o.appendChild(document.createTextNode(t))}}var pt=`
#intro_box[data-v-5d3b253c] {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 99999;
}
#intro_box > .top[data-v-5d3b253c] {
  width: 100%;
}
#intro_box > .content[data-v-5d3b253c] {
  width: 100%;
}
#intro_box > .content > .left[data-v-5d3b253c] {
  position: absolute;
  left: 0;
}
#intro_box > .content > .original-box[data-v-5d3b253c] {
  position: absolute;
  background-color: transparent;
  transition: all 0.3s cubic-bezier(0, 0, 0.58, 1);
}
#intro_box > .content > .original-box .round[data-v-5d3b253c] {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  opacity: 0.65;
  background-color: #9900ff;
}
#intro_box > .content > .original-box .round-flicker[data-v-5d3b253c]:before,
#intro_box > .content > .original-box .round-flicker[data-v-5d3b253c]:after {
  content: '';
  width: 100%;
  height: 100%;
  position: absolute;
  left: -1px;
  top: -1px;
  box-shadow: #9900ff 0px 0px 2px 2px;
  border: 1px solid rgba(153, 0, 255, 0.5);
  border-radius: 50%;
  animation: warn-5d3b253c 2s linear 0s infinite;
}
@keyframes warn-5d3b253c {
0% {
    transform: scale(0.5);
    opacity: 1;
}
25% {
    transform: scale(1);
    opacity: 0.75;
}
50% {
    transform: scale(1.5);
    opacity: 0.5;
}
75% {
    transform: scale(2);
    opacity: 0.25;
}
100% {
    transform: scale(2.5);
    opacity: 0;
}
}
#intro_box > .content > .tip-box[data-v-5d3b253c] {
  position: absolute;
  /*宽度应为内容宽*/
  width: fit-content;
  max-width: 300px;
  box-sizing: border-box;
  /*高度应为内容高度*/
  height: fit-content;
  transition: all 0.3s;
  z-index: 99999;
  padding: 12px;
  font-size: 15px;
}
#intro_box > .content > .tip-box > .tip-content[data-v-5d3b253c] {
  border-radius: 10px;
  overflow: hidden;
  padding: 10px;
  color: #fff;
}
#intro_box > .content > .tip-box > .tip-content > .title[data-v-5d3b253c] {
  font-weight: bold;
  margin-bottom: 10px;
}
#intro_box > .content > .tip-box > .tip-content > .content[data-v-5d3b253c] {
  white-space: normal;
  overflow-wrap: break-word;
  line-height: 1.5;
}
#intro_box > .content > .tip-box > .tip-content > .action[data-v-5d3b253c] {
  margin-top: 15px;
  width: 100%;
  display: flex;
}
#intro_box > .content > .tip-box > .tip-content > .action > .item[data-v-5d3b253c] {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 5px 15px;
  color: #fff;
  font-weight: bold;
  border: 1px solid #ccc;
  margin: 5px;
}
#intro_box > .content > .tip-box > .tip-content > .action > .item.prev[data-v-5d3b253c] {
  color: #ccc;
}
#intro_box > .content > .tip-box > .tip-content > .action > .item.next[data-v-5d3b253c] {
  color: #ccc;
}
#intro_box > .content > .tip-box > .tip-content > .action > .item.done[data-v-5d3b253c] {
  color: #ccc;
}
#intro_box > .content > .tip-box > .tip-content > .action > .item.skip[data-v-5d3b253c] {
  color: #ccc;
}
#intro_box > .content > .right[data-v-5d3b253c] {
  position: absolute;
  background-color: rgba(0, 0, 0, 0.9);
}
#intro_box > .bottom[data-v-5d3b253c] {
  width: 100%;
  background-color: rgba(0, 0, 0, 0.9);
}
`;dt(pt);_.render=ct;_.__scopeId="data-v-5d3b253c";var ut=(()=>{const t=_;return t.install=e=>{e.component("Vue3IntroStep",t)},t})();const Bt={__name:"BeginnerGuide",setup(t){const e=P(null),n=P(!1),i={backgroundOpacity:.8,titleStyle:{textAlign:"left",fontSize:"18px"},contentStyle:{textAlign:"left",fontSize:"14px"},tips:[{el:"#toggleTheme",tipPosition:"bottom",title:"切换系统主题",content:"一键开启护眼模式"},{el:"#fullscreen",tipPosition:"bottom",title:"全屏/退出全屏",content:"一键开启全屏"},{el:"#theme-setting",tipPosition:"bottom",title:"设置主题色",content:"调整为你喜欢的主题色"},{el:"#user-dropdown",tipPosition:"bottom",title:"个人中心",content:"查看个人资料和退出系统"},{el:"#menu-collapse",tipPosition:"bottom",title:"展开/收起菜单",content:"一键展开/收起菜单"},{el:"#top-tab",tipPosition:"bottom",title:"标签栏",content:"鼠标滚轮滑动可调整至最佳视野"},{el:"#layout-setting",tipPosition:"left",title:"调整系统布局",content:"将系统布局调整为你喜欢的样子"}]};function o(){n.value=!1}function d(){n.value=!1}function p(){e.value.next()}function m(){e.value.prev()}return(v,r)=>{const u=z,c=j;return g(),w(A,null,[x(u,{trigger:"hover"},{trigger:l(()=>[a("i",{class:"i-fe:beginner mr-16 cursor-pointer text-20",onClick:r[0]||(r[0]=f=>n.value=!0)})]),default:l(()=>[r[2]||(r[2]=y(" 操作指引 ",-1))]),_:1}),x(s(ut),{ref_key:"myIntroStep",ref:e,show:s(n),"onUpdate:show":r[1]||(r[1]=f=>R(n)?n.value=f:null),config:i},{prev:l(({tipItem:f,index:h})=>[x(c,{class:"mr-12",type:"primary",color:"#fff","text-color":"#fff",ghost:"",round:"",size:"small",onClick:$=>m(f,h)},{default:l(()=>[...r[3]||(r[3]=[y(" 上一步 ",-1)])]),_:1},8,["onClick"])]),next:l(({tipItem:f})=>[x(c,{class:"mr-12",type:"primary",color:"#fff","text-color":"#fff",ghost:"",round:"",size:"small",onClick:h=>p(f)},{default:l(()=>[...r[4]||(r[4]=[y(" 下一步 ",-1)])]),_:1},8,["onClick"])]),skip:l(()=>[x(c,{type:"primary",color:"#fff","text-color":"#fff",ghost:"",round:"",size:"small",onClick:o},{default:l(()=>[...r[5]||(r[5]=[y(" 跳过 ",-1)])]),_:1})]),done:l(()=>[x(c,{type:"primary",color:"#fff","text-color":"#fff",ghost:"",round:"",size:"small",onClick:d},{default:l(()=>[...r[6]||(r[6]=[y(" 完成 ",-1)])]),_:1})]),_:1},8,["show"])],64)}}},It={__name:"Fullscreen",setup(t){const{isFullscreen:e,toggle:n}=V();return(i,o)=>(g(),w("i",{id:"fullscreen",class:F(["mr-16 cursor-pointer",s(e)?"i-fe:minimize":"i-fe:maximize"]),onClick:o[0]||(o[0]=(...d)=>s(n)&&s(n)(...d))},null,2))}},ft={__name:"ContextMenu",props:{show:{type:Boolean,default:!1},currentPath:{type:String,default:""},x:{type:Number,default:0},y:{type:Number,default:0}},emits:["update:show"],setup(t,{emit:e}){const n=t,i=e,o=T(),d=q(()=>[{label:"重新加载",key:"reload",disabled:n.currentPath!==o.activeTab,icon:()=>k("i",{class:"i-mdi:refresh text-14"})},{label:"关闭",key:"close",disabled:o.tabs.length<=1,icon:()=>k("i",{class:"i-mdi:close text-14"})},{label:"关闭其他",key:"close-other",disabled:o.tabs.length<=1,icon:()=>k("i",{class:"i-mdi:arrow-expand-horizontal text-14"})},{label:"关闭左侧",key:"close-left",disabled:o.tabs.length<=1||n.currentPath===o.tabs[0].path,icon:()=>k("i",{class:"i-mdi:arrow-expand-left text-14"})},{label:"关闭右侧",key:"close-right",disabled:o.tabs.length<=1||n.currentPath===o.tabs[o.tabs.length-1].path,icon:()=>k("i",{class:"i-mdi:arrow-expand-right text-14"})}]),p=D(),m=new Map([["reload",()=>{o.reloadTab(p.fullPath,p.meta?.keepAlive)}],["close",()=>{o.removeTab(n.currentPath)}],["close-other",()=>{o.removeOther(n.currentPath)}],["close-left",()=>{o.removeLeft(n.currentPath)}],["close-right",()=>{o.removeRight(n.currentPath)}]]);function v(){i("update:show",!1)}function r(u){const c=m.get(u);typeof c=="function"&&c(),v()}return(u,c)=>{const f=J;return g(),S(f,{show:t.show,options:s(d),x:t.x,y:t.y,placement:"bottom-start",onClickoutside:v,onSelect:r},null,8,["show","options","x","y"])}}},ht={id:"top-tab"},gt={__name:"index",setup(t){const e=U(),n=T(),i=W({show:!1,x:0,y:0,currentPath:""});function o(r){n.setActiveTab(r),e.push(r)}function d(){i.show=!0}function p(){i.show=!1}function m(r,u,c){Object.assign(i,{x:r,y:u,currentPath:c})}async function v(r,u){const{clientX:c,clientY:f}=r;p(),m(c,f,u.path),await Y(),d()}return(r,u)=>{const c=Q,f=Z;return g(),w("div",ht,[x(f,{value:s(n).activeTab,closable:s(n).tabs.length>1,type:"card",onClose:u[0]||(u[0]=h=>s(n).removeTab(h))},{default:l(()=>[(g(!0),w(A,null,H(s(n).tabs,h=>(g(),S(c,{key:h.path,name:h.path,onClick:$=>o(h.path),onContextmenu:L($=>v($,h),["prevent"])},{default:l(()=>[y(C(h.title),1)]),_:2},1032,["name","onClick","onContextmenu"]))),128))]),_:1},8,["value","closable"]),s(i).show?(g(),S(ft,{key:0,show:s(i).show,"onUpdate:show":u[1]||(u[1]=h=>s(i).show=h),"current-path":s(i).currentPath,x:s(i).x,y:s(i).y},null,8,["show","current-path","x","y"])):B("",!0)])}}},St=K(gt,[["__scopeId","data-v-0d851a24"]]),mt={class:"f-c-c"},$t={__name:"ThemeSetting",setup(t){const e=G(),n=Object.entries(X.getPresetColors()).map(([,i])=>i.primary);return(i,o)=>{const d=tt,p=z;return g(),w("div",mt,[x(p,{trigger:"hover",placement:"bottom"},{trigger:l(()=>[x(d,{id:"theme-setting",class:"h-32 w-32",value:s(e).primaryColor,swatches:s(n),"on-update:value":m=>s(e).setPrimaryColor(m),"render-label":()=>""},null,8,["value","swatches","on-update:value"])]),default:l(()=>[o[0]||(o[0]=y(" 设置主题色 ",-1))]),_:1})])}}};export{St as A,Bt as _,It as a,$t as b};

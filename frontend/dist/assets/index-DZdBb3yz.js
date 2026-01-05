import{_ as ne}from"./CommonPage-BfuSNnRr.js";import{_ as le}from"./_plugin-vue_export-helper-DlAUqK2U.js";import{a$ as se,d2 as ae,a as i,c as l,b6 as de,b7 as ce,d as C,ab as me,f as W,h,u as D,g as j,d3 as be,bA as ge,j as R,bb as _,k as J,b as u,e as ue,p as pe,bl as H,s as K,q as ve,aG as he,d4 as fe,b3 as xe,l as w,z as L,A as p,B as z,J as t,C as y,H as S,D as m,aA as T,F as v,N as P,K as G,G as B,I as k,E as ze}from"./index-B-Gu03yZ.js";import{N as Ce}from"./Tag-D6BM4hDc.js";import"./TheFooter-De6Lei8R.js";import"./AppCard-D4RQj772.js";function ye(e){const{textColor3:r,infoColor:s,errorColor:a,successColor:n,warningColor:b,textColor1:f,textColor2:d,railColor:c,fontWeightStrong:g,fontSize:$}=e;return Object.assign(Object.assign({},ae),{contentFontSize:$,titleFontWeight:g,circleBorder:`2px solid ${r}`,circleBorderInfo:`2px solid ${s}`,circleBorderError:`2px solid ${a}`,circleBorderSuccess:`2px solid ${n}`,circleBorderWarning:`2px solid ${b}`,iconColor:r,iconColorInfo:s,iconColorError:a,iconColorSuccess:n,iconColorWarning:b,titleTextColor:f,contentTextColor:d,metaTextColor:r,lineColor:c})}const _e={common:se,self:ye},$e=i([l("table",`
 font-size: var(--n-font-size);
 font-variant-numeric: tabular-nums;
 line-height: var(--n-line-height);
 width: 100%;
 border-radius: var(--n-border-radius) var(--n-border-radius) 0 0;
 text-align: left;
 border-collapse: separate;
 border-spacing: 0;
 overflow: hidden;
 background-color: var(--n-td-color);
 border-color: var(--n-merged-border-color);
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 --n-merged-border-color: var(--n-border-color);
 `,[i("th",`
 white-space: nowrap;
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 text-align: inherit;
 padding: var(--n-th-padding);
 vertical-align: inherit;
 text-transform: none;
 border: 0px solid var(--n-merged-border-color);
 font-weight: var(--n-th-font-weight);
 color: var(--n-th-text-color);
 background-color: var(--n-th-color);
 border-bottom: 1px solid var(--n-merged-border-color);
 border-right: 1px solid var(--n-merged-border-color);
 `,[i("&:last-child",`
 border-right: 0px solid var(--n-merged-border-color);
 `)]),i("td",`
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 padding: var(--n-td-padding);
 color: var(--n-td-text-color);
 background-color: var(--n-td-color);
 border: 0px solid var(--n-merged-border-color);
 border-right: 1px solid var(--n-merged-border-color);
 border-bottom: 1px solid var(--n-merged-border-color);
 `,[i("&:last-child",`
 border-right: 0px solid var(--n-merged-border-color);
 `)]),C("bordered",`
 border: 1px solid var(--n-merged-border-color);
 border-radius: var(--n-border-radius);
 `,[i("tr",[i("&:last-child",[i("td",`
 border-bottom: 0 solid var(--n-merged-border-color);
 `)])])]),C("single-line",[i("th",`
 border-right: 0px solid var(--n-merged-border-color);
 `),i("td",`
 border-right: 0px solid var(--n-merged-border-color);
 `)]),C("single-column",[i("tr",[i("&:not(:last-child)",[i("td",`
 border-bottom: 0px solid var(--n-merged-border-color);
 `)])])]),C("striped",[i("tr:nth-of-type(even)",[i("td","background-color: var(--n-td-color-striped)")])]),me("bottom-bordered",[i("tr",[i("&:last-child",[i("td",`
 border-bottom: 0px solid var(--n-merged-border-color);
 `)])])])]),de(l("table",`
 background-color: var(--n-td-color-modal);
 --n-merged-border-color: var(--n-border-color-modal);
 `,[i("th",`
 background-color: var(--n-th-color-modal);
 `),i("td",`
 background-color: var(--n-td-color-modal);
 `)])),ce(l("table",`
 background-color: var(--n-td-color-popover);
 --n-merged-border-color: var(--n-border-color-popover);
 `,[i("th",`
 background-color: var(--n-th-color-popover);
 `),i("td",`
 background-color: var(--n-td-color-popover);
 `)]))]),ke=Object.assign(Object.assign({},j.props),{bordered:{type:Boolean,default:!0},bottomBordered:{type:Boolean,default:!0},singleLine:{type:Boolean,default:!0},striped:Boolean,singleColumn:Boolean,size:{type:String,default:"medium"}}),we=W({name:"Table",props:ke,setup(e){const{mergedClsPrefixRef:r,inlineThemeDisabled:s,mergedRtlRef:a}=D(e),n=j("Table","-table",$e,be,e,r),b=ge("Table",a,r),f=R(()=>{const{size:c}=e,{self:{borderColor:g,tdColor:$,tdColorModal:o,tdColorPopover:x,thColor:N,thColorModal:E,thColorPopover:V,thTextColor:F,tdTextColor:I,borderRadius:O,thFontWeight:M,lineHeight:A,borderColorModal:U,borderColorPopover:X,tdColorStriped:Y,tdColorStripedModal:Z,tdColorStripedPopover:ee,[_("fontSize",c)]:te,[_("tdPadding",c)]:oe,[_("thPadding",c)]:re},common:{cubicBezierEaseInOut:ie}}=n.value;return{"--n-bezier":ie,"--n-td-color":$,"--n-td-color-modal":o,"--n-td-color-popover":x,"--n-td-text-color":I,"--n-border-color":g,"--n-border-color-modal":U,"--n-border-color-popover":X,"--n-border-radius":O,"--n-font-size":te,"--n-th-color":N,"--n-th-color-modal":E,"--n-th-color-popover":V,"--n-th-font-weight":M,"--n-th-text-color":F,"--n-line-height":A,"--n-td-padding":oe,"--n-th-padding":re,"--n-td-color-striped":Y,"--n-td-color-striped-modal":Z,"--n-td-color-striped-popover":ee}}),d=s?J("table",R(()=>e.size[0]),f,e):void 0;return{rtlEnabled:b,mergedClsPrefix:r,cssVars:s?void 0:f,themeClass:d?.themeClass,onRender:d?.onRender}},render(){var e;const{mergedClsPrefix:r}=this;return(e=this.onRender)===null||e===void 0||e.call(this),h("table",{class:[`${r}-table`,this.themeClass,{[`${r}-table--rtl`]:this.rtlEnabled,[`${r}-table--bottom-bordered`]:this.bottomBordered,[`${r}-table--bordered`]:this.bordered,[`${r}-table--single-line`]:this.singleLine,[`${r}-table--single-column`]:this.singleColumn,[`${r}-table--striped`]:this.striped}],style:this.cssVars},this.$slots)}}),q=1.25,Se=l("timeline",`
 position: relative;
 width: 100%;
 display: flex;
 flex-direction: column;
 line-height: ${q};
`,[C("horizontal",`
 flex-direction: row;
 `,[i(">",[l("timeline-item",`
 flex-shrink: 0;
 padding-right: 40px;
 `,[C("dashed-line-type",[i(">",[l("timeline-item-timeline",[u("line",`
 background-image: linear-gradient(90deg, var(--n-color-start), var(--n-color-start) 50%, transparent 50%, transparent 100%);
 background-size: 10px 1px;
 `)])])]),i(">",[l("timeline-item-content",`
 margin-top: calc(var(--n-icon-size) + 12px);
 `,[i(">",[u("meta",`
 margin-top: 6px;
 margin-bottom: unset;
 `)])]),l("timeline-item-timeline",`
 width: 100%;
 height: calc(var(--n-icon-size) + 12px);
 `,[u("line",`
 left: var(--n-icon-size);
 top: calc(var(--n-icon-size) / 2 - 1px);
 right: 0px;
 width: unset;
 height: 2px;
 `)])])])])]),C("right-placement",[l("timeline-item",[l("timeline-item-content",`
 text-align: right;
 margin-right: calc(var(--n-icon-size) + 12px);
 `),l("timeline-item-timeline",`
 width: var(--n-icon-size);
 right: 0;
 `)])]),C("left-placement",[l("timeline-item",[l("timeline-item-content",`
 margin-left: calc(var(--n-icon-size) + 12px);
 `),l("timeline-item-timeline",`
 left: 0;
 `)])]),l("timeline-item",`
 position: relative;
 `,[i("&:last-child",[l("timeline-item-timeline",[u("line",`
 display: none;
 `)]),l("timeline-item-content",[u("meta",`
 margin-bottom: 0;
 `)])]),l("timeline-item-content",[u("title",`
 margin: var(--n-title-margin);
 font-size: var(--n-title-font-size);
 transition: color .3s var(--n-bezier);
 font-weight: var(--n-title-font-weight);
 color: var(--n-title-text-color);
 `),u("content",`
 transition: color .3s var(--n-bezier);
 font-size: var(--n-content-font-size);
 color: var(--n-content-text-color);
 `),u("meta",`
 transition: color .3s var(--n-bezier);
 font-size: 12px;
 margin-top: 6px;
 margin-bottom: 20px;
 color: var(--n-meta-text-color);
 `)]),C("dashed-line-type",[l("timeline-item-timeline",[u("line",`
 --n-color-start: var(--n-line-color);
 transition: --n-color-start .3s var(--n-bezier);
 background-color: transparent;
 background-image: linear-gradient(180deg, var(--n-color-start), var(--n-color-start) 50%, transparent 50%, transparent 100%);
 background-size: 1px 10px;
 `)])]),l("timeline-item-timeline",`
 width: calc(var(--n-icon-size) + 12px);
 position: absolute;
 top: calc(var(--n-title-font-size) * ${q} / 2 - var(--n-icon-size) / 2);
 height: 100%;
 `,[u("circle",`
 border: var(--n-circle-border);
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 width: var(--n-icon-size);
 height: var(--n-icon-size);
 border-radius: var(--n-icon-size);
 box-sizing: border-box;
 `),u("icon",`
 color: var(--n-icon-color);
 font-size: var(--n-icon-size);
 height: var(--n-icon-size);
 width: var(--n-icon-size);
 display: flex;
 align-items: center;
 justify-content: center;
 `),u("line",`
 transition: background-color .3s var(--n-bezier);
 position: absolute;
 top: var(--n-icon-size);
 left: calc(var(--n-icon-size) / 2 - 1px);
 bottom: 0px;
 width: 2px;
 background-color: var(--n-line-color);
 `)])])]),Te=Object.assign(Object.assign({},j.props),{horizontal:Boolean,itemPlacement:{type:String,default:"left"},size:{type:String,default:"medium"},iconSize:Number}),Q=ue("n-timeline"),Pe=W({name:"Timeline",props:Te,setup(e,{slots:r}){const{mergedClsPrefixRef:s}=D(e),a=j("Timeline","-timeline",Se,_e,e,s);return pe(Q,{props:e,mergedThemeRef:a,mergedClsPrefixRef:s}),()=>{const{value:n}=s;return h("div",{class:[`${n}-timeline`,e.horizontal&&`${n}-timeline--horizontal`,`${n}-timeline--${e.size}-size`,!e.horizontal&&`${n}-timeline--${e.itemPlacement}-placement`]},r)}}}),Be={time:[String,Number],title:String,content:String,color:String,lineType:{type:String,default:"default"},type:{type:String,default:"default"}},Re=W({name:"TimelineItem",props:Be,slots:Object,setup(e){const r=ve(Q);r||he("timeline-item","`n-timeline-item` must be placed inside `n-timeline`."),fe();const{inlineThemeDisabled:s}=D(),a=R(()=>{const{props:{size:b,iconSize:f},mergedThemeRef:d}=r,{type:c}=e,{self:{titleTextColor:g,contentTextColor:$,metaTextColor:o,lineColor:x,titleFontWeight:N,contentFontSize:E,[_("iconSize",b)]:V,[_("titleMargin",b)]:F,[_("titleFontSize",b)]:I,[_("circleBorder",c)]:O,[_("iconColor",c)]:M},common:{cubicBezierEaseInOut:A}}=d.value;return{"--n-bezier":A,"--n-circle-border":O,"--n-icon-color":M,"--n-content-font-size":E,"--n-content-text-color":$,"--n-line-color":x,"--n-meta-text-color":o,"--n-title-font-size":I,"--n-title-font-weight":N,"--n-title-margin":F,"--n-title-text-color":g,"--n-icon-size":xe(f)||V}}),n=s?J("timeline-item",R(()=>{const{props:{size:b,iconSize:f}}=r,{type:d}=e;return`${b[0]}${f||"a"}${d[0]}`}),a,r.props):void 0;return{mergedClsPrefix:r.mergedClsPrefixRef,cssVars:s?void 0:a,themeClass:n?.themeClass,onRender:n?.onRender}},render(){const{mergedClsPrefix:e,color:r,onRender:s,$slots:a}=this;return s?.(),h("div",{class:[`${e}-timeline-item`,this.themeClass,`${e}-timeline-item--${this.type}-type`,`${e}-timeline-item--${this.lineType}-line-type`],style:this.cssVars},h("div",{class:`${e}-timeline-item-timeline`},h("div",{class:`${e}-timeline-item-timeline__line`}),H(a.icon,n=>n?h("div",{class:`${e}-timeline-item-timeline__icon`,style:{color:r}},n):h("div",{class:`${e}-timeline-item-timeline__circle`,style:{borderColor:r}}))),h("div",{class:`${e}-timeline-item-content`},H(a.header,n=>n||this.title?h("div",{class:`${e}-timeline-item-content__title`},n||this.title):null),h("div",{class:`${e}-timeline-item-content__content`},K(a.default,()=>[this.content])),h("div",{class:`${e}-timeline-item-content__meta`},K(a.footer,()=>[this.time]))))}}),je={class:"grid grid-cols-4 gap-16 mb-24"},Ne={class:"flex items-center justify-between"},Ee={class:"text-gray-400 text-14 mb-8"},Ve={class:"text-12 mt-4"},Fe={class:"grid grid-cols-2 gap-16 mb-24"},Ie={class:"h-280"},Oe={class:"flex items-end justify-between h-full pt-20"},Me={class:"text-12 text-gray-400 mt-8"},Ae={class:"h-280 flex items-center justify-center"},Le={class:"flex items-center gap-32"},We={class:"space-y-12"},De={class:"text-14"},He={class:"text-gray-400 text-12 ml-8"},Ke={class:"grid grid-cols-2 gap-16"},Ge={class:"max-w-200 truncate"},qe=Object.assign({name:"Dashboard"},{__name:"index",setup(e){const r=w([{title:"总视频数",value:"1,286",trend:12.5,icon:"i-fe:video",color:"#316C72",bgColor:"rgba(49, 108, 114, 0.1)"},{title:"总播放量",value:"892.3K",trend:8.2,icon:"i-fe:play-circle",color:"#67C23A",bgColor:"rgba(103, 194, 58, 0.1)"},{title:"注册用户",value:"15,847",trend:-2.4,icon:"i-fe:users",color:"#E6A23C",bgColor:"rgba(230, 162, 60, 0.1)"},{title:"今日收益",value:"¥3,286",trend:18.6,icon:"i-fe:dollar-sign",color:"#F56C6C",bgColor:"rgba(245, 108, 108, 0.1)"}]),s=w([{day:"周一",value:12e3},{day:"周二",value:18e3},{day:"周三",value:15e3},{day:"周四",value:22e3},{day:"周五",value:28e3},{day:"周六",value:35e3},{day:"周日",value:32e3}]),a=R(()=>Math.max(...s.value.map(c=>c.value))),n=w([{name:"电影",percent:35,color:"#316C72"},{name:"电视剧",percent:25,color:"#67C23A"},{name:"动漫",percent:20,color:"#E6A23C"},{name:"综艺",percent:12,color:"#F56C6C"},{name:"其他",percent:8,color:"#909399"}]),b=w([{id:1,title:"【4K修复】经典老电影合集",views:128e3,likes:8900},{id:2,title:"2024年度热门番剧盘点",views:95e3,likes:6700},{id:3,title:"最新韩剧推荐TOP10",views:82e3,likes:5400},{id:4,title:"周星驰经典喜剧全集",views:76e3,likes:4800},{id:5,title:"纪录片：地球脉动",views:68e3,likes:4200}]),f=w([{type:"success",title:"新视频上传",content:"用户 test123 上传了视频《春节特辑》",time:"2分钟前"},{type:"info",title:"用户注册",content:"新用户 movie_lover 完成注册",time:"15分钟前"},{type:"warning",title:"视频审核",content:"视频《游戏实况》需要审核",time:"30分钟前"},{type:"error",title:"举报处理",content:"用户举报视频ID #1234 侵权",time:"1小时前"},{type:"success",title:"收益到账",content:"今日广告收益 ¥286.50 已到账",time:"2小时前"}]);function d(c){return c>=1e4?(c/1e4).toFixed(1)+"w":c.toLocaleString()}return(c,g)=>{const $=ne;return p(),L($,null,{default:z(()=>[t("div",je,[(p(!0),y(B,null,S(m(r),o=>(p(),L(m(T),{key:o.title,class:"stat-card",hoverable:""},{default:z(()=>[t("div",Ne,[t("div",null,[t("p",Ee,v(o.title),1),t("p",{class:"text-28 font-bold",style:P({color:o.color})},v(o.value),5),t("p",Ve,[t("span",{class:G(o.trend>0?"text-green-500":"text-red-500")},v(o.trend>0?"↑":"↓")+" "+v(Math.abs(o.trend))+"% ",3),g[0]||(g[0]=t("span",{class:"text-gray-400 ml-4"},"较昨日",-1))])]),t("div",{class:"w-48 h-48 rounded-full flex items-center justify-center",style:P({backgroundColor:o.bgColor})},[t("i",{class:G(`${o.icon} text-24`),style:P({color:o.color})},null,6)],4)])]),_:2},1024))),128))]),t("div",Fe,[k(m(T),{title:"播放量趋势 (近7天)"},{default:z(()=>[t("div",Ie,[t("div",Oe,[(p(!0),y(B,null,S(m(s),(o,x)=>(p(),y("div",{key:x,class:"flex flex-col items-center flex-1"},[t("div",{class:"w-32 rounded-t-4 transition-all duration-300 hover:opacity-80",style:P({height:`${o.value/m(a)*200}px`,backgroundColor:"#316C72"})},null,4),t("p",Me,v(o.day),1)]))),128))])])]),_:1}),k(m(T),{title:"视频分类占比"},{default:z(()=>[t("div",Ae,[t("div",Le,[g[1]||(g[1]=t("div",{class:"relative w-160 h-160"},[t("div",{class:"pie-chart"})],-1)),t("div",We,[(p(!0),y(B,null,S(m(n),o=>(p(),y("div",{key:o.name,class:"flex items-center gap-8"},[t("span",{class:"w-12 h-12 rounded-full",style:P({backgroundColor:o.color})},null,4),t("span",De,v(o.name),1),t("span",He,v(o.percent)+"%",1)]))),128))])])])]),_:1})]),t("div",Ke,[k(m(T),{title:"热门视频 TOP 5"},{default:z(()=>[k(m(we),{bordered:!1,"single-line":!1,size:"small"},{default:z(()=>[g[2]||(g[2]=t("thead",null,[t("tr",null,[t("th",null,"排名"),t("th",null,"视频标题"),t("th",null,"播放量"),t("th",null,"点赞")])],-1)),t("tbody",null,[(p(!0),y(B,null,S(m(b),(o,x)=>(p(),y("tr",{key:o.id},[t("td",null,[k(m(Ce),{type:x<3?"warning":"default",size:"small"},{default:z(()=>[ze(v(x+1),1)]),_:2},1032,["type"])]),t("td",Ge,v(o.title),1),t("td",null,v(d(o.views)),1),t("td",null,v(d(o.likes)),1)]))),128))])]),_:1})]),_:1}),k(m(T),{title:"最新动态"},{default:z(()=>[k(m(Pe),null,{default:z(()=>[(p(!0),y(B,null,S(m(f),(o,x)=>(p(),L(m(Re),{key:x,type:o.type,title:o.title,content:o.content,time:o.time},null,8,["type","title","content","time"]))),128))]),_:1})]),_:1})])]),_:1})}}}),et=le(qe,[["__scopeId","data-v-9671b986"]]);export{et as default};

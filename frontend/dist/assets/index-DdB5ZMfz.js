import{c as g,a as _,b as C,d as N,e as V,f as T,h as k,u as H,g as P,i as O,p as F,t as K,j as y,k as M,l as E,o as q,m as G,n as J,q as U,s as Q,v as X,w as Y,x as Z,y as W,z as w,A as v,B as $,C as z,D as s,E as S,F as L,G as I,H as ee,I as l,J as h,K as j,L as re,r as te}from"./index-B-Gu03yZ.js";import{_ as oe,a as ne,b as ae,c as se}from"./UserAvatar-2hx7tV6f.js";import{_ as ie,a as ce,b as le,A as me}from"./ThemeSetting-BaEcEw0E.js";import{_ as de}from"./AppCard-D4RQj772.js";import{_ as ue}from"./ToggleTheme-CeRuYrih.js";import{N as pe}from"./Dropdown-02AqhRlH.js";import"./_plugin-vue_export-helper-DlAUqK2U.js";import"./create-d3gso5YV.js";import"./index-B1VvpShT.js";import"./RadioGroup-DmquGkZ-.js";import"./RadioButton-Kgy5Xtw7.js";import"./Avatar-DrFk2b0A.js";import"./utils-wPkz17gm.js";import"./Tag-D6BM4hDc.js";import"./Tabs-CySlKHww.js";import"./Add-B2HqXqx-.js";import"./toNumber-B15YBfZq.js";import"./ColorPicker-DJHWI4-E.js";import"./Input-Bfmkx-Cv.js";import"./use-locale-ChhWjjuj.js";import"./Suffix-BtjUzbwI.js";import"./Icon-DN5ERbeL.js";const fe=g("breadcrumb",`
 white-space: nowrap;
 cursor: default;
 line-height: var(--n-item-line-height);
`,[_("ul",`
 list-style: none;
 padding: 0;
 margin: 0;
 `),_("a",`
 color: inherit;
 text-decoration: inherit;
 `),g("breadcrumb-item",`
 font-size: var(--n-font-size);
 transition: color .3s var(--n-bezier);
 display: inline-flex;
 align-items: center;
 `,[g("icon",`
 font-size: 18px;
 vertical-align: -.2em;
 transition: color .3s var(--n-bezier);
 color: var(--n-item-text-color);
 `),_("&:not(:last-child)",[N("clickable",[C("link",`
 cursor: pointer;
 `,[_("&:hover",`
 background-color: var(--n-item-color-hover);
 `),_("&:active",`
 background-color: var(--n-item-color-pressed); 
 `)])])]),C("link",`
 padding: 4px;
 border-radius: var(--n-item-border-radius);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 color: var(--n-item-text-color);
 position: relative;
 `,[_("&:hover",`
 color: var(--n-item-text-color-hover);
 `,[g("icon",`
 color: var(--n-item-text-color-hover);
 `)]),_("&:active",`
 color: var(--n-item-text-color-pressed);
 `,[g("icon",`
 color: var(--n-item-text-color-pressed);
 `)])]),C("separator",`
 margin: 0 8px;
 color: var(--n-separator-color);
 transition: color .3s var(--n-bezier);
 user-select: none;
 -webkit-user-select: none;
 `),_("&:last-child",[C("link",`
 font-weight: var(--n-font-weight-active);
 cursor: unset;
 color: var(--n-item-text-color-active);
 `,[g("icon",`
 color: var(--n-item-text-color-active);
 `)]),C("separator",`
 display: none;
 `)])])]),A=V("n-breadcrumb"),_e=Object.assign(Object.assign({},P.props),{separator:{type:String,default:"/"}}),he=T({name:"Breadcrumb",props:_e,setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:t}=H(e),n=P("Breadcrumb","-breadcrumb",fe,O,e,o);F(A,{separatorRef:K(e,"separator"),mergedClsPrefixRef:o});const i=y(()=>{const{common:{cubicBezierEaseInOut:u},self:{separatorColor:b,itemTextColor:d,itemTextColorHover:r,itemTextColorPressed:c,itemTextColorActive:p,fontSize:m,fontWeightActive:x,itemBorderRadius:f,itemColorHover:B,itemColorPressed:R,itemLineHeight:D}}=n.value;return{"--n-font-size":m,"--n-bezier":u,"--n-item-text-color":d,"--n-item-text-color-hover":r,"--n-item-text-color-pressed":c,"--n-item-text-color-active":p,"--n-separator-color":b,"--n-item-color-hover":B,"--n-item-color-pressed":R,"--n-item-border-radius":f,"--n-font-weight-active":x,"--n-item-line-height":D}}),a=t?M("breadcrumb",void 0,i,e):void 0;return{mergedClsPrefix:o,cssVars:t?void 0:i,themeClass:a?.themeClass,onRender:a?.onRender}},render(){var e;return(e=this.onRender)===null||e===void 0||e.call(this),k("nav",{class:[`${this.mergedClsPrefix}-breadcrumb`,this.themeClass],style:this.cssVars,"aria-label":"Breadcrumb"},k("ul",null,this.$slots))}});function be(e=J?window:null){const o=()=>{const{hash:i,host:a,hostname:u,href:b,origin:d,pathname:r,port:c,protocol:p,search:m}=e?.location||{};return{hash:i,host:a,hostname:u,href:b,origin:d,pathname:r,port:c,protocol:p,search:m}},t=E(o()),n=()=>{t.value=o()};return q(()=>{e&&(e.addEventListener("popstate",n),e.addEventListener("hashchange",n))}),G(()=>{e&&(e.removeEventListener("popstate",n),e.removeEventListener("hashchange",n))}),t}const ve={separator:String,href:String,clickable:{type:Boolean,default:!0},onClick:Function},xe=T({name:"BreadcrumbItem",props:ve,slots:Object,setup(e,{slots:o}){const t=U(A,null);if(!t)return()=>null;const{separatorRef:n,mergedClsPrefixRef:i}=t,a=be(),u=y(()=>e.href?"a":"span"),b=y(()=>a.value.href===e.href?"location":null);return()=>{const{value:d}=i;return k("li",{class:[`${d}-breadcrumb-item`,e.clickable&&`${d}-breadcrumb-item--clickable`]},k(u.value,{class:`${d}-breadcrumb-item__link`,"aria-current":b.value,href:e.href,onClick:e.onClick},o),k("span",{class:`${d}-breadcrumb-item__separator`,"aria-hidden":"true"},Q(o.separator,()=>{var r;return[(r=e.separator)!==null&&r!==void 0?r:n.value]})))}}}),ge={class:"flex items-center"},ke={__name:"BreadCrumb",setup(e){const o=X(),t=Y(),n=Z(),i=E([]);W(()=>t.name,r=>{i.value=a(n.permissions,r)},{immediate:!0});function a(r,c,p=[]){for(const m of r){if(m.code===c)return[...p,m];if(m.children?.length){const x=a(m.children,c,[...p,m]);if(x)return x}}return null}function u(r){r.path&&r.code!==t.name&&o.push(r.path)}function b(r=[]){return r.filter(c=>c.show).map(c=>({label:c.name,key:c.code,icon:()=>k("i",{class:c.icon})}))}function d(r){r&&r!==t.name&&o.push({name:r})}return(r,c)=>{const p=xe,m=pe,x=he;return v(),w(x,null,{default:$(()=>[s(i)?.length?(v(!0),z(I,{key:1},ee(s(i),(f,B)=>(v(),w(p,{key:f.code,clickable:!!f.path,onClick:R=>u(f)},{default:$(()=>[l(m,{options:B<s(i).length-1?b(f.children):[],onSelect:d},{default:$(()=>[h("div",ge,[h("i",{class:j([f.icon,"mr-8"])},null,2),S(" "+L(f.name),1)])]),_:2},1032,["options"])]),_:2},1032,["clickable","onClick"]))),128)):(v(),w(p,{key:0,clickable:!1},{default:$(()=>[S(L(s(t).meta.title),1)]),_:1}))]),_:1})}}},Ce={class:"ml-auto flex flex-shrink-0 items-center px-12 text-18"},$e={__name:"index",setup(e){function o(t){window.open(t)}return(t,n)=>{const i=le,a=de;return v(),w(a,{class:"flex items-center px-12","border-b":"1px solid light_border dark:dark_border"},{default:$(()=>[l(s(oe)),l(s(ke)),h("div",Ce,[l(s(ie)),l(s(ue)),l(s(ce)),h("i",{class:"i-fe:github mr-16 cursor-pointer",onClick:n[0]||(n[0]=u=>o("https://github.com/zclzone/vue-naive-admin/tree/2.x"))}),h("i",{class:"i-me:gitee mr-16 cursor-pointer",onClick:n[1]||(n[1]=u=>o("https://gitee.com/isme-admin/vue-naive-admin/tree/2.x"))}),l(i,{class:"mr-16"}),l(s(ne))])]),_:1})}}},we={__name:"index",setup(e){return(o,t)=>(v(),z(I,null,[l(s(ae),{"border-b":"1px solid light_border dark:dark_border"}),l(s(se),{class:"cus-scroll-y mt-4 h-0 flex-1"})],64))}},Be={class:"wh-full flex"},ye={class:"w-0 flex-col flex-1"},ze={class:"p-12","border-b":"1px solid light_border dark:dark_border"},Xe={__name:"index",setup(e){const o=re();return(t,n)=>(v(),z("div",Be,[h("aside",{class:j(["flex-col flex-shrink-0 transition-width-300",s(o).collapsed?"w-64":"w-220"]),"border-r":"1px solid light_border dark:dark_border"},[l(we)],2),h("article",ye,[l($e,{class:"h-60 flex-shrink-0"}),h("div",ze,[l(s(me),{class:"flex-shrink-0"})]),te(t.$slots,"default")])]))}};export{Xe as default};

import{c as f,d as l,a as d,f as v,bR as b,h,ap as y,u as C,g as m,cp as _,j as a,k as $,b3 as z}from"./index-CktGYKsc.js";const R=f("icon",`
 height: 1em;
 width: 1em;
 line-height: 1em;
 text-align: center;
 display: inline-block;
 position: relative;
 fill: currentColor;
`,[l("color-transition",{transition:"color .3s var(--n-bezier)"}),l("depth",{color:"var(--n-color)"},[d("svg",{opacity:"var(--n-opacity)",transition:"opacity .3s var(--n-bezier)"})]),d("svg",{height:"1em",width:"1em"})]),S=Object.assign(Object.assign({},m.props),{depth:[String,Number],size:[Number,String],color:String,component:[Object,Function]}),P=v({_n_icon__:!0,name:"Icon",inheritAttrs:!1,props:S,setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:t}=C(e),s=m("Icon","-icon",R,_,e,o),r=a(()=>{const{depth:i}=e,{common:{cubicBezierEaseInOut:c},self:u}=s.value;if(i!==void 0){const{color:p,[`opacity${i}Depth`]:g}=u;return{"--n-bezier":c,"--n-color":p,"--n-opacity":g}}return{"--n-bezier":c,"--n-color":"","--n-opacity":""}}),n=t?$("icon",a(()=>`${e.depth||"d"}`),r,e):void 0;return{mergedClsPrefix:o,mergedStyle:a(()=>{const{size:i,color:c}=e;return{fontSize:z(i),color:c}}),cssVars:t?void 0:r,themeClass:n?.themeClass,onRender:n?.onRender}},render(){var e;const{$parent:o,depth:t,mergedClsPrefix:s,component:r,onRender:n,themeClass:i}=this;return!((e=o?.$options)===null||e===void 0)&&e._n_icon__&&b("icon","don't wrap `n-icon` inside `n-icon`"),n?.(),h("i",y(this.$attrs,{role:"img",class:[`${s}-icon`,i,{[`${s}-icon--depth`]:t,[`${s}-icon--color-transition`]:t!==void 0}],style:[this.cssVars,this.mergedStyle]}),r?h(r):this.$slots)}});export{P as N};

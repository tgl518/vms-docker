import{c,a as x,b as v,f as P,h as i,u as He,bX as at,bY as V,bZ as F,b_ as Y,b$ as ge,c0 as H,c1 as N,c2 as O,c3 as oe,c4 as W,c5 as Be,c6 as Ce,c7 as Ue,c8 as Ae,l as S,j as $,aP as ne,aQ as ae,e as lt,q as Te,al as qe,c9 as se,ca as $e,cb as Re,cc as _e,bR as it,b1 as st,d as De,bg as ut,bh as dt,bi as ct,bj as Ve,T as ht,a6 as pt,bk as ft,bm as bt,g as Ee,cd as gt,p as mt,t as Se,am as Me,y as vt,bb as Ie,k as xt,b5 as kt,aO as wt,aq as ce,R as he,Y as yt}from"./index-B-Gu03yZ.js";import{_ as St}from"./Input-Bfmkx-Cv.js";import{u as Ct}from"./use-locale-ChhWjjuj.js";const Ut=c("input-group",`
 display: inline-flex;
 width: 100%;
 flex-wrap: nowrap;
 vertical-align: bottom;
`,[x(">",[c("input",[x("&:not(:last-child)",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `),x("&:not(:first-child)",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 margin-left: -1px!important;
 `)]),c("button",[x("&:not(:last-child)",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `,[v("state-border, border",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `)]),x("&:not(:first-child)",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `,[v("state-border, border",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `)])]),x("*",[x("&:not(:last-child)",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `,[x(">",[c("input",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `),c("base-selection",[c("base-selection-label",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `),c("base-selection-tags",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `),v("box-shadow, border, state-border",`
 border-top-right-radius: 0!important;
 border-bottom-right-radius: 0!important;
 `)])])]),x("&:not(:first-child)",`
 margin-left: -1px!important;
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `,[x(">",[c("input",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `),c("base-selection",[c("base-selection-label",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `),c("base-selection-tags",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `),v("box-shadow, border, state-border",`
 border-top-left-radius: 0!important;
 border-bottom-left-radius: 0!important;
 `)])])])])])]),At={},$t=P({name:"InputGroup",props:At,setup(e){const{mergedClsPrefixRef:t}=He(e);return at("-input-group",Ut,t),{mergedClsPrefix:t}},render(){const{mergedClsPrefix:e}=this;return i("div",{class:`${e}-input-group`},this.$slots)}});function Rt(e,t){switch(e[0]){case"hex":return t?"#000000FF":"#000000";case"rgb":return t?"rgba(0, 0, 0, 1)":"rgb(0, 0, 0)";case"hsl":return t?"hsla(0, 0%, 0%, 1)":"hsl(0, 0%, 0%)";case"hsv":return t?"hsva(0, 0%, 0%, 1)":"hsv(0, 0%, 0%)"}return"#000000"}function ue(e){return e===null?null:/^ *#/.test(e)?"hex":e.includes("rgb")?"rgb":e.includes("hsl")?"hsl":e.includes("hsv")?"hsv":null}function _t(e,t=[255,255,255],l="AA"){const[o,a,n,s]=V(F(e));if(s===1){const L=pe([o,a,n]),D=pe(t);return(Math.max(L,D)+.05)/(Math.min(L,D)+.05)>=(l==="AA"?4.5:7)}const u=Math.round(o*s+t[0]*(1-s)),p=Math.round(a*s+t[1]*(1-s)),z=Math.round(n*s+t[2]*(1-s)),B=pe([u,p,z]),R=pe(t);return(Math.max(B,R)+.05)/(Math.min(B,R)+.05)>=(l==="AA"?4.5:7)}function pe(e){const[t,l,o]=e.map(a=>(a/=255,a<=.03928?a/12.92:Math.pow((a+.055)/1.055,2.4)));return .2126*t+.7152*l+.0722*o}function Vt(e){return e=Math.round(e),e>=360?359:e<0?0:e}function Pt(e){return e=Math.round(e*100)/100,e>1?1:e<0?0:e}const zt={rgb:{hex(e){return O(V(e))},hsl(e){const[t,l,o,a]=V(e);return F([...Ae(t,l,o),a])},hsv(e){const[t,l,o,a]=V(e);return W([...Ue(t,l,o),a])}},hex:{rgb(e){return H(V(e))},hsl(e){const[t,l,o,a]=V(e);return F([...Ae(t,l,o),a])},hsv(e){const[t,l,o,a]=V(e);return W([...Ue(t,l,o),a])}},hsl:{hex(e){const[t,l,o,a]=oe(e);return O([...Ce(t,l,o),a])},rgb(e){const[t,l,o,a]=oe(e);return H([...Ce(t,l,o),a])},hsv(e){const[t,l,o,a]=oe(e);return W([...Be(t,l,o),a])}},hsv:{hex(e){const[t,l,o,a]=Y(e);return O([...N(t,l,o),a])},rgb(e){const[t,l,o,a]=Y(e);return H([...N(t,l,o),a])},hsl(e){const[t,l,o,a]=Y(e);return F([...ge(t,l,o),a])}}};function Ne(e,t,l){return l=l||ue(e),l?l===t?e:zt[l][t](e):null}const ie="12px",Dt=12,Z="6px",Mt=P({name:"AlphaSlider",props:{clsPrefix:{type:String,required:!0},rgba:{type:Array,default:null},alpha:{type:Number,default:0},onUpdateAlpha:{type:Function,required:!0},onComplete:Function},setup(e){const t=S(null);function l(n){!t.value||!e.rgba||(ne("mousemove",document,o),ne("mouseup",document,a),o(n))}function o(n){const{value:s}=t;if(!s)return;const{width:u,left:p}=s.getBoundingClientRect(),z=(n.clientX-p)/(u-Dt);e.onUpdateAlpha(Pt(z))}function a(){var n;ae("mousemove",document,o),ae("mouseup",document,a),(n=e.onComplete)===null||n===void 0||n.call(e)}return{railRef:t,railBackgroundImage:$(()=>{const{rgba:n}=e;return n?`linear-gradient(to right, rgba(${n[0]}, ${n[1]}, ${n[2]}, 0) 0%, rgba(${n[0]}, ${n[1]}, ${n[2]}, 1) 100%)`:""}),handleMouseDown:l}},render(){const{clsPrefix:e}=this;return i("div",{class:`${e}-color-picker-slider`,ref:"railRef",style:{height:ie,borderRadius:Z},onMousedown:this.handleMouseDown},i("div",{style:{borderRadius:Z,position:"absolute",left:0,right:0,top:0,bottom:0,overflow:"hidden"}},i("div",{class:`${e}-color-picker-checkboard`}),i("div",{class:`${e}-color-picker-slider__image`,style:{backgroundImage:this.railBackgroundImage}})),this.rgba&&i("div",{style:{position:"absolute",left:Z,right:Z,top:0,bottom:0}},i("div",{class:`${e}-color-picker-handle`,style:{left:`calc(${this.alpha*100}% - ${Z})`,borderRadius:Z,width:ie,height:ie}},i("div",{class:`${e}-color-picker-handle__fill`,style:{backgroundColor:H(this.rgba),borderRadius:Z,width:ie,height:ie}}))))}}),Pe=lt("n-color-picker");function It(e){return/^\d{1,3}\.?\d*$/.test(e.trim())?Math.max(0,Math.min(Number.parseInt(e),255)):!1}function Ft(e){return/^\d{1,3}\.?\d*$/.test(e.trim())?Math.max(0,Math.min(Number.parseInt(e),360)):!1}function Ht(e){return/^\d{1,3}\.?\d*$/.test(e.trim())?Math.max(0,Math.min(Number.parseInt(e),100)):!1}function Bt(e){const t=e.trim();return/^#[0-9a-fA-F]+$/.test(t)?[4,5,7,9].includes(t.length):!1}function Tt(e){return/^\d{1,3}\.?\d*%$/.test(e.trim())?Math.max(0,Math.min(Number.parseInt(e)/100,100)):!1}const qt={paddingSmall:"0 4px"},Fe=P({name:"ColorInputUnit",props:{label:{type:String,required:!0},value:{type:[Number,String],default:null},showAlpha:Boolean,onUpdateValue:{type:Function,required:!0}},setup(e){const t=S(""),{themeRef:l}=Te(Pe,null);qe(()=>{t.value=o()});function o(){const{value:s}=e;if(s===null)return"";const{label:u}=e;return u==="HEX"?s:u==="A"?`${Math.floor(s*100)}%`:String(Math.floor(s))}function a(s){t.value=s}function n(s){let u,p;switch(e.label){case"HEX":p=Bt(s),p&&e.onUpdateValue(s),t.value=o();break;case"H":u=Ft(s),u===!1?t.value=o():e.onUpdateValue(u);break;case"S":case"L":case"V":u=Ht(s),u===!1?t.value=o():e.onUpdateValue(u);break;case"A":u=Tt(s),u===!1?t.value=o():e.onUpdateValue(u);break;case"R":case"G":case"B":u=It(s),u===!1?t.value=o():e.onUpdateValue(u);break}}return{mergedTheme:l,inputValue:t,handleInputChange:n,handleInputUpdateValue:a}},render(){const{mergedTheme:e}=this;return i(St,{size:"small",placeholder:this.label,theme:e.peers.Input,themeOverrides:e.peerOverrides.Input,builtinThemeOverrides:qt,value:this.inputValue,onUpdateValue:this.handleInputUpdateValue,onChange:this.handleInputChange,style:this.label==="A"?"flex-grow: 1.25;":""})}}),Et=P({name:"ColorInput",props:{clsPrefix:{type:String,required:!0},mode:{type:String,required:!0},modes:{type:Array,required:!0},showAlpha:{type:Boolean,required:!0},value:{type:String,default:null},valueArr:{type:Array,default:null},onUpdateValue:{type:Function,required:!0},onUpdateMode:{type:Function,required:!0}},setup(e){return{handleUnitUpdateValue(t,l){const{showAlpha:o}=e;if(e.mode==="hex"){e.onUpdateValue((o?O:se)(l));return}let a;switch(e.valueArr===null?a=[0,0,0,0]:a=Array.from(e.valueArr),e.mode){case"hsv":a[t]=l,e.onUpdateValue((o?W:_e)(a));break;case"rgb":a[t]=l,e.onUpdateValue((o?H:Re)(a));break;case"hsl":a[t]=l,e.onUpdateValue((o?F:$e)(a));break}}}},render(){const{clsPrefix:e,modes:t}=this;return i("div",{class:`${e}-color-picker-input`},i("div",{class:`${e}-color-picker-input__mode`,onClick:this.onUpdateMode,style:{cursor:t.length===1?"":"pointer"}},this.mode.toUpperCase()+(this.showAlpha?"A":"")),i($t,null,{default:()=>{const{mode:l,valueArr:o,showAlpha:a}=this;if(l==="hex"){let n=null;try{n=o===null?null:(a?O:se)(o)}catch{}return i(Fe,{label:"HEX",showAlpha:a,value:n,onUpdateValue:s=>{this.handleUnitUpdateValue(0,s)}})}return(l+(a?"a":"")).split("").map((n,s)=>i(Fe,{label:n.toUpperCase(),value:o===null?null:o[s],onUpdateValue:u=>{this.handleUnitUpdateValue(s,u)}}))}}))}});function Nt(e,t){if(t==="hsv"){const[l,o,a,n]=Y(e);return H([...N(l,o,a),n])}return e}function Ot(e){const t=document.createElement("canvas").getContext("2d");return t?(t.fillStyle=e,t.fillStyle):"#000000"}const jt=P({name:"ColorPickerSwatches",props:{clsPrefix:{type:String,required:!0},mode:{type:String,required:!0},swatches:{type:Array,required:!0},onUpdateColor:{type:Function,required:!0}},setup(e){const t=$(()=>e.swatches.map(n=>{const s=ue(n);return{value:n,mode:s,legalValue:Nt(n,s)}}));function l(n){const{mode:s}=e;let{value:u,mode:p}=n;return p||(p="hex",/^[a-zA-Z]+$/.test(u)?u=Ot(u):(it("color-picker",`color ${u} in swatches is invalid.`),u="#000000")),p===s?u:Ne(u,s,p)}function o(n){e.onUpdateColor(l(n))}function a(n,s){n.key==="Enter"&&o(s)}return{parsedSwatchesRef:t,handleSwatchSelect:o,handleSwatchKeyDown:a}},render(){const{clsPrefix:e}=this;return i("div",{class:`${e}-color-picker-swatches`},this.parsedSwatchesRef.map(t=>i("div",{class:`${e}-color-picker-swatch`,tabindex:0,onClick:()=>{this.handleSwatchSelect(t)},onKeydown:l=>{this.handleSwatchKeyDown(l,t)}},i("div",{class:`${e}-color-picker-swatch__fill`,style:{background:t.legalValue}}))))}}),Lt=P({name:"ColorPickerTrigger",slots:Object,props:{clsPrefix:{type:String,required:!0},value:{type:String,default:null},hsla:{type:Array,default:null},disabled:Boolean,onClick:Function},setup(e){const{colorPickerSlots:t,renderLabelRef:l}=Te(Pe,null);return()=>{const{hsla:o,value:a,clsPrefix:n,onClick:s,disabled:u}=e,p=t.label||l.value;return i("div",{class:[`${n}-color-picker-trigger`,u&&`${n}-color-picker-trigger--disabled`],onClick:u?void 0:s},i("div",{class:`${n}-color-picker-trigger__fill`},i("div",{class:`${n}-color-picker-checkboard`}),i("div",{style:{position:"absolute",left:0,right:0,top:0,bottom:0,backgroundColor:o?F(o):""}}),a&&o?i("div",{class:`${n}-color-picker-trigger__value`,style:{color:_t(o)?"white":"black"}},p?p(a):a):null))}}}),Gt=P({name:"ColorPreview",props:{clsPrefix:{type:String,required:!0},mode:{type:String,required:!0},color:{type:String,default:null,validator:e=>{const t=ue(e);return!!(!e||t&&t!=="hsv")}},onUpdateColor:{type:Function,required:!0}},setup(e){function t(l){var o;const a=l.target.value;(o=e.onUpdateColor)===null||o===void 0||o.call(e,Ne(a.toUpperCase(),e.mode,"hex")),l.stopPropagation()}return{handleChange:t}},render(){const{clsPrefix:e}=this;return i("div",{class:`${e}-color-picker-preview__preview`},i("span",{class:`${e}-color-picker-preview__fill`,style:{background:this.color||"#000000"}}),i("input",{class:`${e}-color-picker-preview__input`,type:"color",value:this.color,onChange:this.handleChange}))}}),re="12px",Xt=12,K="6px",Zt=6,Kt="linear-gradient(90deg,red,#ff0 16.66%,#0f0 33.33%,#0ff 50%,#00f 66.66%,#f0f 83.33%,red)",Yt=P({name:"HueSlider",props:{clsPrefix:{type:String,required:!0},hue:{type:Number,required:!0},onUpdateHue:{type:Function,required:!0},onComplete:Function},setup(e){const t=S(null);function l(n){t.value&&(ne("mousemove",document,o),ne("mouseup",document,a),o(n))}function o(n){const{value:s}=t;if(!s)return;const{width:u,left:p}=s.getBoundingClientRect(),z=Vt((n.clientX-p-Zt)/(u-Xt)*360);e.onUpdateHue(z)}function a(){var n;ae("mousemove",document,o),ae("mouseup",document,a),(n=e.onComplete)===null||n===void 0||n.call(e)}return{railRef:t,handleMouseDown:l}},render(){const{clsPrefix:e}=this;return i("div",{class:`${e}-color-picker-slider`,style:{height:re,borderRadius:K}},i("div",{ref:"railRef",style:{boxShadow:"inset 0 0 2px 0 rgba(0, 0, 0, .24)",boxSizing:"border-box",backgroundImage:Kt,height:re,borderRadius:K,position:"relative"},onMousedown:this.handleMouseDown},i("div",{style:{position:"absolute",left:K,right:K,top:0,bottom:0}},i("div",{class:`${e}-color-picker-handle`,style:{left:`calc((${this.hue}%) / 359 * 100 - ${K})`,borderRadius:K,width:re,height:re}},i("div",{class:`${e}-color-picker-handle__fill`,style:{backgroundColor:`hsl(${this.hue}, 100%, 50%)`,borderRadius:K,width:re,height:re}})))))}}),fe="12px",be="6px",Wt=P({name:"Pallete",props:{clsPrefix:{type:String,required:!0},rgba:{type:Array,default:null},displayedHue:{type:Number,required:!0},displayedSv:{type:Array,required:!0},onUpdateSV:{type:Function,required:!0},onComplete:Function},setup(e){const t=S(null);function l(n){t.value&&(ne("mousemove",document,o),ne("mouseup",document,a),o(n))}function o(n){const{value:s}=t;if(!s)return;const{width:u,height:p,left:z,bottom:B}=s.getBoundingClientRect(),R=(B-n.clientY)/p,j=(n.clientX-z)/u,L=100*(j>1?1:j<0?0:j),D=100*(R>1?1:R<0?0:R);e.onUpdateSV(L,D)}function a(){var n;ae("mousemove",document,o),ae("mouseup",document,a),(n=e.onComplete)===null||n===void 0||n.call(e)}return{palleteRef:t,handleColor:$(()=>{const{rgba:n}=e;return n?`rgb(${n[0]}, ${n[1]}, ${n[2]})`:""}),handleMouseDown:l}},render(){const{clsPrefix:e}=this;return i("div",{class:`${e}-color-picker-pallete`,onMousedown:this.handleMouseDown,ref:"palleteRef"},i("div",{class:`${e}-color-picker-pallete__layer`,style:{backgroundImage:`linear-gradient(90deg, white, hsl(${this.displayedHue}, 100%, 50%))`}}),i("div",{class:`${e}-color-picker-pallete__layer ${e}-color-picker-pallete__layer--shadowed`,style:{backgroundImage:"linear-gradient(180deg, rgba(0, 0, 0, 0%), rgba(0, 0, 0, 100%))"}}),this.rgba&&i("div",{class:`${e}-color-picker-handle`,style:{width:fe,height:fe,borderRadius:be,left:`calc(${this.displayedSv[0]}% - ${be})`,bottom:`calc(${this.displayedSv[1]}% - ${be})`}},i("div",{class:`${e}-color-picker-handle__fill`,style:{backgroundColor:this.handleColor,borderRadius:be,width:fe,height:fe}})))}}),Qt=x([c("color-picker",`
 display: inline-block;
 box-sizing: border-box;
 height: var(--n-height);
 font-size: var(--n-font-size);
 width: 100%;
 position: relative;
 `),c("color-picker-panel",`
 margin: 4px 0;
 width: 240px;
 font-size: var(--n-panel-font-size);
 color: var(--n-text-color);
 background-color: var(--n-color);
 transition:
 box-shadow .3s var(--n-bezier),
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 border-radius: var(--n-border-radius);
 box-shadow: var(--n-box-shadow);
 `,[st(),c("input",`
 text-align: center;
 `)]),c("color-picker-checkboard",`
 background: white; 
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `,[x("&::after",`
 background-image: linear-gradient(45deg, #DDD 25%, #0000 25%), linear-gradient(-45deg, #DDD 25%, #0000 25%), linear-gradient(45deg, #0000 75%, #DDD 75%), linear-gradient(-45deg, #0000 75%, #DDD 75%);
 background-size: 12px 12px;
 background-position: 0 0, 0 6px, 6px -6px, -6px 0px;
 background-repeat: repeat;
 content: "";
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),c("color-picker-slider",`
 margin-bottom: 8px;
 position: relative;
 box-sizing: border-box;
 `,[v("image",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `),x("&::after",`
 content: "";
 position: absolute;
 border-radius: inherit;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 box-shadow: inset 0 0 2px 0 rgba(0, 0, 0, .24);
 pointer-events: none;
 `)]),c("color-picker-handle",`
 z-index: 1;
 box-shadow: 0 0 2px 0 rgba(0, 0, 0, .45);
 position: absolute;
 background-color: white;
 overflow: hidden;
 `,[v("fill",`
 box-sizing: border-box;
 border: 2px solid white;
 `)]),c("color-picker-pallete",`
 height: 180px;
 position: relative;
 margin-bottom: 8px;
 cursor: crosshair;
 `,[v("layer",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `,[De("shadowed",`
 box-shadow: inset 0 0 2px 0 rgba(0, 0, 0, .24);
 `)])]),c("color-picker-preview",`
 display: flex;
 `,[v("sliders",`
 flex: 1 0 auto;
 `),v("preview",`
 position: relative;
 height: 30px;
 width: 30px;
 margin: 0 0 8px 6px;
 border-radius: 50%;
 box-shadow: rgba(0, 0, 0, .15) 0px 0px 0px 1px inset;
 overflow: hidden;
 `),v("fill",`
 display: block;
 width: 30px;
 height: 30px;
 `),v("input",`
 position: absolute;
 top: 0;
 left: 0;
 width: 30px;
 height: 30px;
 opacity: 0;
 z-index: 1;
 `)]),c("color-picker-input",`
 display: flex;
 align-items: center;
 `,[c("input",`
 flex-grow: 1;
 flex-basis: 0;
 `),v("mode",`
 width: 72px;
 text-align: center;
 `)]),c("color-picker-control",`
 padding: 12px;
 `),c("color-picker-action",`
 display: flex;
 margin-top: -4px;
 border-top: 1px solid var(--n-divider-color);
 padding: 8px 12px;
 justify-content: flex-end;
 `,[c("button","margin-left: 8px;")]),c("color-picker-trigger",`
 border: var(--n-border);
 height: 100%;
 box-sizing: border-box;
 border-radius: var(--n-border-radius);
 transition: border-color .3s var(--n-bezier);
 cursor: pointer;
 `,[v("value",`
 white-space: nowrap;
 position: relative;
 `),v("fill",`
 border-radius: var(--n-border-radius);
 position: absolute;
 display: flex;
 align-items: center;
 justify-content: center;
 left: 4px;
 right: 4px;
 top: 4px;
 bottom: 4px;
 `),De("disabled","cursor: not-allowed"),c("color-picker-checkboard",`
 border-radius: var(--n-border-radius);
 `,[x("&::after",`
 --n-block-size: calc((var(--n-height) - 8px) / 3);
 background-size: calc(var(--n-block-size) * 2) calc(var(--n-block-size) * 2);
 background-position: 0 0, 0 var(--n-block-size), var(--n-block-size) calc(-1 * var(--n-block-size)), calc(-1 * var(--n-block-size)) 0px; 
 `)])]),c("color-picker-swatches",`
 display: grid;
 grid-gap: 8px;
 flex-wrap: wrap;
 position: relative;
 grid-template-columns: repeat(auto-fill, 18px);
 margin-top: 10px;
 `,[c("color-picker-swatch",`
 width: 18px;
 height: 18px;
 background-image: linear-gradient(45deg, #DDD 25%, #0000 25%), linear-gradient(-45deg, #DDD 25%, #0000 25%), linear-gradient(45deg, #0000 75%, #DDD 75%), linear-gradient(-45deg, #0000 75%, #DDD 75%);
 background-size: 8px 8px;
 background-position: 0px 0, 0px 4px, 4px -4px, -4px 0px;
 background-repeat: repeat;
 `,[v("fill",`
 position: relative;
 width: 100%;
 height: 100%;
 border-radius: 3px;
 box-shadow: rgba(0, 0, 0, .15) 0px 0px 0px 1px inset;
 cursor: pointer;
 `),x("&:focus",`
 outline: none;
 `,[v("fill",[x("&::after",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 left: 0;
 background: inherit;
 filter: blur(2px);
 content: "";
 `)])])])])]),Jt=Object.assign(Object.assign({},Ee.props),{value:String,show:{type:Boolean,default:void 0},defaultShow:Boolean,defaultValue:String,modes:{type:Array,default:()=>["rgb","hex","hsl"]},placement:{type:String,default:"bottom-start"},to:Ve.propTo,showAlpha:{type:Boolean,default:!0},showPreview:Boolean,swatches:Array,disabled:{type:Boolean,default:void 0},actions:{type:Array,default:null},internalActions:Array,size:String,renderLabel:Function,onComplete:Function,onConfirm:Function,onClear:Function,"onUpdate:show":[Function,Array],onUpdateShow:[Function,Array],"onUpdate:value":[Function,Array],onUpdateValue:[Function,Array]}),or=P({name:"ColorPicker",props:Jt,slots:Object,setup(e,{slots:t}){const l=S(null);let o=null;const a=bt(e),{mergedSizeRef:n,mergedDisabledRef:s}=a,{localeRef:u}=Ct("global"),{mergedClsPrefixRef:p,namespaceRef:z,inlineThemeDisabled:B}=He(e),R=Ee("ColorPicker","-color-picker",Qt,gt,e,p);mt(Pe,{themeRef:R,renderLabelRef:Se(e,"renderLabel"),colorPickerSlots:t});const j=S(e.defaultShow),L=Me(Se(e,"show"),j);function D(r){const{onUpdateShow:d,"onUpdate:show":f}=e;d&&ce(d,r),f&&ce(f,r),j.value=r}const{defaultValue:me}=e,ze=S(me===void 0?Rt(e.modes,e.showAlpha):me),w=Me(Se(e,"value"),ze),Q=S([w.value]),M=S(0),ve=$(()=>ue(w.value)),{modes:Oe}=e,_=S(ue(w.value)||Oe[0]||"rgb");function je(){const{modes:r}=e,{value:d}=_,f=r.findIndex(b=>b===d);~f?_.value=r[(f+1)%r.length]:_.value="rgb"}let C,U,J,ee,T,q,E,A;const le=$(()=>{const{value:r}=w;if(!r)return null;switch(ve.value){case"hsv":return Y(r);case"hsl":return[C,U,J,A]=oe(r),[...Be(C,U,J),A];case"rgb":case"hex":return[T,q,E,A]=V(r),[...Ue(T,q,E),A]}}),G=$(()=>{const{value:r}=w;if(!r)return null;switch(ve.value){case"rgb":case"hex":return V(r);case"hsv":return[C,U,ee,A]=Y(r),[...N(C,U,ee),A];case"hsl":return[C,U,J,A]=oe(r),[...Ce(C,U,J),A]}}),xe=$(()=>{const{value:r}=w;if(!r)return null;switch(ve.value){case"hsl":return oe(r);case"hsv":return[C,U,ee,A]=Y(r),[...ge(C,U,ee),A];case"rgb":case"hex":return[T,q,E,A]=V(r),[...Ae(T,q,E),A]}}),Le=$(()=>{switch(_.value){case"rgb":case"hex":return G.value;case"hsv":return le.value;case"hsl":return xe.value}}),de=S(0),ke=S(1),we=S([0,0]);function Ge(r,d){const{value:f}=le,b=de.value,g=f?f[3]:1;we.value=[r,d];const{showAlpha:h}=e;switch(_.value){case"hsv":m((h?W:_e)([b,r,d,g]),"cursor");break;case"hsl":m((h?F:$e)([...ge(b,r,d),g]),"cursor");break;case"rgb":m((h?H:Re)([...N(b,r,d),g]),"cursor");break;case"hex":m((h?O:se)([...N(b,r,d),g]),"cursor");break}}function Xe(r){de.value=r;const{value:d}=le;if(!d)return;const[,f,b,g]=d,{showAlpha:h}=e;switch(_.value){case"hsv":m((h?W:_e)([r,f,b,g]),"cursor");break;case"rgb":m((h?H:Re)([...N(r,f,b),g]),"cursor");break;case"hex":m((h?O:se)([...N(r,f,b),g]),"cursor");break;case"hsl":m((h?F:$e)([...ge(r,f,b),g]),"cursor");break}}function Ze(r){switch(_.value){case"hsv":[C,U,ee]=le.value,m(W([C,U,ee,r]),"cursor");break;case"rgb":[T,q,E]=G.value,m(H([T,q,E,r]),"cursor");break;case"hex":[T,q,E]=G.value,m(O([T,q,E,r]),"cursor");break;case"hsl":[C,U,J]=xe.value,m(F([C,U,J,r]),"cursor");break}ke.value=r}function m(r,d){d==="cursor"?o=r:o=null;const{nTriggerFormChange:f,nTriggerFormInput:b}=a,{onUpdateValue:g,"onUpdate:value":h}=e;g&&ce(g,r),h&&ce(h,r),f(),b(),ze.value=r}function Ke(r){m(r,"input"),yt(te)}function te(r=!0){const{value:d}=w;if(d){const{nTriggerFormChange:f,nTriggerFormInput:b}=a,{onComplete:g}=e;g&&g(d);const{value:h}=Q,{value:y}=M;r&&(h.splice(y+1,h.length,d),M.value=y+1),f(),b()}}function Ye(){const{value:r}=M;r-1<0||(m(Q.value[r-1],"input"),te(!1),M.value=r-1)}function We(){const{value:r}=M;r<0||r+1>=Q.value.length||(m(Q.value[r+1],"input"),te(!1),M.value=r+1)}function Qe(){m(null,"input");const{onClear:r}=e;r&&r(),D(!1)}function Je(){const{value:r}=w,{onConfirm:d}=e;d&&d(r),D(!1)}const et=$(()=>M.value>=1),tt=$(()=>{const{value:r}=Q;return r.length>1&&M.value<r.length-1});vt(L,r=>{r||(Q.value=[w.value],M.value=0)}),qe(()=>{if(!(o&&o===w.value)){const{value:r}=le;r&&(de.value=r[0],ke.value=r[3],we.value=[r[1],r[2]])}o=null});const ye=$(()=>{const{value:r}=n,{common:{cubicBezierEaseInOut:d},self:{textColor:f,color:b,panelFontSize:g,boxShadow:h,border:y,borderRadius:k,dividerColor:X,[Ie("height",r)]:ot,[Ie("fontSize",r)]:nt}}=R.value;return{"--n-bezier":d,"--n-text-color":f,"--n-color":b,"--n-panel-font-size":g,"--n-font-size":nt,"--n-box-shadow":h,"--n-border":y,"--n-border-radius":k,"--n-height":ot,"--n-divider-color":X}}),I=B?xt("color-picker",$(()=>n.value[0]),ye,e):void 0;function rt(){var r;const{value:d}=G,{value:f}=de,{internalActions:b,modes:g,actions:h}=e,{value:y}=R,{value:k}=p;return i("div",{class:[`${k}-color-picker-panel`,I?.themeClass.value],onDragstart:X=>{X.preventDefault()},style:B?void 0:ye.value},i("div",{class:`${k}-color-picker-control`},i(Wt,{clsPrefix:k,rgba:d,displayedHue:f,displayedSv:we.value,onUpdateSV:Ge,onComplete:te}),i("div",{class:`${k}-color-picker-preview`},i("div",{class:`${k}-color-picker-preview__sliders`},i(Yt,{clsPrefix:k,hue:f,onUpdateHue:Xe,onComplete:te}),e.showAlpha?i(Mt,{clsPrefix:k,rgba:d,alpha:ke.value,onUpdateAlpha:Ze,onComplete:te}):null),e.showPreview?i(Gt,{clsPrefix:k,mode:_.value,color:G.value&&se(G.value),onUpdateColor:X=>{m(X,"input")}}):null),i(Et,{clsPrefix:k,showAlpha:e.showAlpha,mode:_.value,modes:g,onUpdateMode:je,value:w.value,valueArr:Le.value,onUpdateValue:Ke}),((r=e.swatches)===null||r===void 0?void 0:r.length)&&i(jt,{clsPrefix:k,mode:_.value,swatches:e.swatches,onUpdateColor:X=>{m(X,"input")}})),h?.length?i("div",{class:`${k}-color-picker-action`},h.includes("confirm")&&i(he,{size:"small",onClick:Je,theme:y.peers.Button,themeOverrides:y.peerOverrides.Button},{default:()=>u.value.confirm}),h.includes("clear")&&i(he,{size:"small",onClick:Qe,disabled:!w.value,theme:y.peers.Button,themeOverrides:y.peerOverrides.Button},{default:()=>u.value.clear})):null,t.action?i("div",{class:`${k}-color-picker-action`},{default:t.action}):b?i("div",{class:`${k}-color-picker-action`},b.includes("undo")&&i(he,{size:"small",onClick:Ye,disabled:!et.value,theme:y.peers.Button,themeOverrides:y.peerOverrides.Button},{default:()=>u.value.undo}),b.includes("redo")&&i(he,{size:"small",onClick:We,disabled:!tt.value,theme:y.peers.Button,themeOverrides:y.peerOverrides.Button},{default:()=>u.value.redo})):null)}return{mergedClsPrefix:p,namespace:z,selfRef:l,hsla:xe,rgba:G,mergedShow:L,mergedDisabled:s,isMounted:kt(),adjustedTo:Ve(e),mergedValue:w,handleTriggerClick(){D(!0)},handleClickOutside(r){var d;!((d=l.value)===null||d===void 0)&&d.contains(wt(r))||D(!1)},renderPanel:rt,cssVars:B?void 0:ye,themeClass:I?.themeClass,onRender:I?.onRender}},render(){const{mergedClsPrefix:e,onRender:t}=this;return t?.(),i("div",{class:[this.themeClass,`${e}-color-picker`],ref:"selfRef",style:this.cssVars},i(ut,null,{default:()=>[i(dt,null,{default:()=>i(Lt,{clsPrefix:e,value:this.mergedValue,hsla:this.hsla,disabled:this.mergedDisabled,onClick:this.handleTriggerClick})}),i(ct,{placement:this.placement,show:this.mergedShow,containerClass:this.namespace,teleportDisabled:this.adjustedTo===Ve.tdkey,to:this.adjustedTo},{default:()=>i(ht,{name:"fade-in-scale-up-transition",appear:this.isMounted},{default:()=>this.mergedShow?pt(this.renderPanel(),[[ft,this.handleClickOutside,void 0,{capture:!0}]]):null})})]}))}});export{or as _};

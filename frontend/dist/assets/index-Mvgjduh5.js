import{aE as $t,aF as Tt,q as Et,aG as Mt,e as Bt,p as At,f as be,h as p,u as ye,aH as jt,l as N,aI as Ot,j as m,o as Me,aJ as it,c as Lt,b as c,d,a as B,aK as rt,aL as Ut,aj as We,aM as He,a6 as Ft,av as Xt,T as Kt,ah as Ze,am as Yt,t as Wt,al as Ht,aN as Zt,y as he,g as ut,k as qt,aO as Gt,aP as oe,Y as qe,aQ as ae,aR as Jt,N as ct,C as X,J as S,I as T,B as F,D as K,M as Qt,R as Ge,G as pe,H as ge,v as en,A as O,F as tn,K as Je,E as Qe,z as et}from"./index-CktGYKsc.js";import{a as $e}from"./video-QVtqaiFM.js";import{a as nn}from"./banner-CllIy5CR.js";import tt from"./AnimeCard-BtjFGz_z.js";import{_ as on}from"./_plugin-vue_export-helper-DlAUqK2U.js";import{c as an}from"./_createCompounder-lDceO_Vy.js";import{N as me}from"./Icon-DyHhfmW1.js";import{_ as sn}from"./Spin-DPibjF7x.js";function ln(e){return $t(Tt(e).toLowerCase())}var nt=an(function(e,o,a){return o=o.toLowerCase(),e+(a?ln(o):o)});const dt=Bt("n-carousel-methods");function rn(e){At(dt,e)}function Be(e="unknown",o="component"){const a=Et(dt);return a||Mt(e,`\`${o}\` must be placed inside \`n-carousel\`.`),a}function un(){return p("svg",{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 16 16"},p("g",{fill:"none"},p("path",{d:"M10.26 3.2a.75.75 0 0 1 .04 1.06L6.773 8l3.527 3.74a.75.75 0 1 1-1.1 1.02l-4-4.25a.75.75 0 0 1 0-1.02l4-4.25a.75.75 0 0 1 1.06-.04z",fill:"currentColor"})))}function cn(){return p("svg",{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 16 16"},p("g",{fill:"none"},p("path",{d:"M5.74 3.2a.75.75 0 0 0-.04 1.06L9.227 8L5.7 11.74a.75.75 0 1 0 1.1 1.02l4-4.25a.75.75 0 0 0 0-1.02l-4-4.25a.75.75 0 0 0-1.06-.04z",fill:"currentColor"})))}const dn=be({name:"CarouselArrow",setup(e){const{mergedClsPrefixRef:o}=ye(e),{isVertical:a,isPrevDisabled:r,isNextDisabled:v,prev:C,next:P}=Be();return{mergedClsPrefix:o,isVertical:a,isPrevDisabled:r,isNextDisabled:v,prev:C,next:P}},render(){const{mergedClsPrefix:e}=this;return p("div",{class:`${e}-carousel__arrow-group`},p("div",{class:[`${e}-carousel__arrow`,this.isPrevDisabled()&&`${e}-carousel__arrow--disabled`],role:"button",onClick:this.prev},un()),p("div",{class:[`${e}-carousel__arrow`,this.isNextDisabled()&&`${e}-carousel__arrow--disabled`],role:"button",onClick:this.next},cn()))}}),fn={total:{type:Number,default:0},currentIndex:{type:Number,default:0},dotType:{type:String,default:"dot"},trigger:{type:String,default:"click"},keyboard:Boolean},vn=be({name:"CarouselDots",props:fn,setup(e){const{mergedClsPrefixRef:o}=ye(e),a=N([]),r=Be();function v(x,f){switch(x.key){case"Enter":case" ":x.preventDefault(),r.to(f);return}e.keyboard&&_(x)}function C(x){e.trigger==="hover"&&r.to(x)}function P(x){e.trigger==="click"&&r.to(x)}function _(x){var f;if(x.shiftKey||x.altKey||x.ctrlKey||x.metaKey)return;const y=(f=document.activeElement)===null||f===void 0?void 0:f.nodeName.toLowerCase();if(y==="input"||y==="textarea")return;const{code:I}=x,A=I==="PageUp"||I==="ArrowUp",L=I==="PageDown"||I==="ArrowDown",k=I==="PageUp"||I==="ArrowRight",h=I==="PageDown"||I==="ArrowLeft",g=r.isVertical(),w=g?A:k,V=g?L:h;!w&&!V||(x.preventDefault(),w&&!r.isNextDisabled()?(r.next(),z(r.currentIndexRef.value)):V&&!r.isPrevDisabled()&&(r.prev(),z(r.currentIndexRef.value)))}function z(x){var f;(f=a.value[x])===null||f===void 0||f.focus()}return Ot(()=>a.value.length=0),{mergedClsPrefix:o,dotEls:a,handleKeydown:v,handleMouseenter:C,handleClick:P}},render(){const{mergedClsPrefix:e,dotEls:o}=this;return p("div",{class:[`${e}-carousel__dots`,`${e}-carousel__dots--${this.dotType}`],role:"tablist"},jt(this.total,a=>{const r=a===this.currentIndex;return p("div",{"aria-selected":r,ref:v=>o.push(v),role:"button",tabindex:"0",class:[`${e}-carousel__dot`,r&&`${e}-carousel__dot--active`],key:a,onClick:()=>{this.handleClick(a)},onMouseenter:()=>{this.handleMouseenter(a)},onKeydown:v=>{this.handleKeydown(v,a)}})}))}}),we="CarouselItem";function hn(e){var o;return((o=e.type)===null||o===void 0?void 0:o.name)===we}const pn=be({name:we,setup(e){const{mergedClsPrefixRef:o}=ye(e),a=Be(nt(we),`n-${nt(we)}`),r=N(),v=m(()=>{const{value:f}=r;return f?a.getSlideIndex(f):-1}),C=m(()=>a.isPrev(v.value)),P=m(()=>a.isNext(v.value)),_=m(()=>a.isActive(v.value)),z=m(()=>a.getSlideStyle(v.value));Me(()=>{a.addSlide(r.value)}),it(()=>{a.removeSlide(r.value)});function x(f){const{value:y}=v;y!==void 0&&a?.onCarouselItemClick(y,f)}return{mergedClsPrefix:o,selfElRef:r,isPrev:C,isNext:P,isActive:_,index:v,style:z,handleClick:x}},render(){var e;const{$slots:o,mergedClsPrefix:a,isPrev:r,isNext:v,isActive:C,index:P,style:_}=this,z=[`${a}-carousel__slide`,{[`${a}-carousel__slide--current`]:C,[`${a}-carousel__slide--prev`]:r,[`${a}-carousel__slide--next`]:v}];return p("div",{ref:"selfElRef",class:z,role:"option",tabindex:"-1","data-index":P,"aria-hidden":!C,style:_,onClickCapture:this.handleClick},(e=o.default)===null||e===void 0?void 0:e.call(o,{isPrev:r,isNext:v,isActive:C,index:P}))}}),gn=Lt("carousel",`
 position: relative;
 width: 100%;
 height: 100%;
 touch-action: pan-y;
 overflow: hidden;
`,[c("slides",`
 display: flex;
 width: 100%;
 height: 100%;
 transition-timing-function: var(--n-bezier);
 transition-property: transform;
 `,[c("slide",`
 flex-shrink: 0;
 position: relative;
 width: 100%;
 height: 100%;
 outline: none;
 overflow: hidden;
 `,[B("> img",`
 display: block;
 `)])]),c("dots",`
 position: absolute;
 display: flex;
 flex-wrap: nowrap;
 `,[d("dot",[c("dot",`
 height: var(--n-dot-size);
 width: var(--n-dot-size);
 background-color: var(--n-dot-color);
 border-radius: 50%;
 cursor: pointer;
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 outline: none;
 `,[B("&:focus",`
 background-color: var(--n-dot-color-focus);
 `),d("active",`
 background-color: var(--n-dot-color-active);
 `)])]),d("line",[c("dot",`
 border-radius: 9999px;
 width: var(--n-dot-line-width);
 height: 4px;
 background-color: var(--n-dot-color);
 cursor: pointer;
 transition:
 width .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 outline: none;
 `,[B("&:focus",`
 background-color: var(--n-dot-color-focus);
 `),d("active",`
 width: var(--n-dot-line-width-active);
 background-color: var(--n-dot-color-active);
 `)])])]),c("arrow",`
 transition: background-color .3s var(--n-bezier);
 cursor: pointer;
 height: 28px;
 width: 28px;
 display: flex;
 align-items: center;
 justify-content: center;
 background-color: rgba(255, 255, 255, .2);
 color: var(--n-arrow-color);
 border-radius: 8px;
 user-select: none;
 -webkit-user-select: none;
 font-size: 18px;
 `,[B("svg",`
 height: 1em;
 width: 1em;
 `),B("&:hover",`
 background-color: rgba(255, 255, 255, .3);
 `)]),d("vertical",`
 touch-action: pan-x;
 `,[c("slides",`
 flex-direction: column;
 `),d("fade",[c("slide",`
 top: 50%;
 left: unset;
 transform: translateY(-50%);
 `)]),d("card",[c("slide",`
 top: 50%;
 left: unset;
 transform: translateY(-50%) translateZ(-400px);
 `,[d("current",`
 transform: translateY(-50%) translateZ(0);
 `),d("prev",`
 transform: translateY(-100%) translateZ(-200px);
 `),d("next",`
 transform: translateY(0%) translateZ(-200px);
 `)])])]),d("usercontrol",[c("slides",[B(">",[B("div",`
 position: absolute;
 top: 50%;
 left: 50%;
 width: 100%;
 height: 100%;
 transform: translate(-50%, -50%);
 `)])])]),d("left",[c("dots",`
 transform: translateY(-50%);
 top: 50%;
 left: 12px;
 flex-direction: column;
 `,[d("line",[c("dot",`
 width: 4px;
 height: var(--n-dot-line-width);
 margin: 4px 0;
 transition:
 height .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 outline: none;
 `,[d("active",`
 height: var(--n-dot-line-width-active);
 `)])])]),c("dot",`
 margin: 4px 0;
 `)]),c("arrow-group",`
 position: absolute;
 display: flex;
 flex-wrap: nowrap;
 `),d("vertical",[c("arrow",`
 transform: rotate(90deg);
 `)]),d("show-arrow",[d("bottom",[c("dots",`
 transform: translateX(0);
 bottom: 18px;
 left: 18px;
 `)]),d("top",[c("dots",`
 transform: translateX(0);
 top: 18px;
 left: 18px;
 `)]),d("left",[c("dots",`
 transform: translateX(0);
 top: 18px;
 left: 18px;
 `)]),d("right",[c("dots",`
 transform: translateX(0);
 top: 18px;
 right: 18px;
 `)])]),d("left",[c("arrow-group",`
 bottom: 12px;
 left: 12px;
 flex-direction: column;
 `,[B("> *:first-child",`
 margin-bottom: 12px;
 `)])]),d("right",[c("dots",`
 transform: translateY(-50%);
 top: 50%;
 right: 12px;
 flex-direction: column;
 `,[d("line",[c("dot",`
 width: 4px;
 height: var(--n-dot-line-width);
 margin: 4px 0;
 transition:
 height .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 outline: none;
 `,[d("active",`
 height: var(--n-dot-line-width-active);
 `)])])]),c("dot",`
 margin: 4px 0;
 `),c("arrow-group",`
 bottom: 12px;
 right: 12px;
 flex-direction: column;
 `,[B("> *:first-child",`
 margin-bottom: 12px;
 `)])]),d("top",[c("dots",`
 transform: translateX(-50%);
 top: 12px;
 left: 50%;
 `,[d("line",[c("dot",`
 margin: 0 4px;
 `)])]),c("dot",`
 margin: 0 4px;
 `),c("arrow-group",`
 top: 12px;
 right: 12px;
 `,[B("> *:first-child",`
 margin-right: 12px;
 `)])]),d("bottom",[c("dots",`
 transform: translateX(-50%);
 bottom: 12px;
 left: 50%;
 `,[d("line",[c("dot",`
 margin: 0 4px;
 `)])]),c("dot",`
 margin: 0 4px;
 `),c("arrow-group",`
 bottom: 12px;
 right: 12px;
 `,[B("> *:first-child",`
 margin-right: 12px;
 `)])]),d("fade",[c("slide",`
 position: absolute;
 opacity: 0;
 transition-property: opacity;
 pointer-events: none;
 `,[d("current",`
 opacity: 1;
 pointer-events: auto;
 `)])]),d("card",[c("slides",`
 perspective: 1000px;
 `),c("slide",`
 position: absolute;
 left: 50%;
 opacity: 0;
 transform: translateX(-50%) translateZ(-400px);
 transition-property: opacity, transform;
 `,[d("current",`
 opacity: 1;
 transform: translateX(-50%) translateZ(0);
 z-index: 1;
 `),d("prev",`
 opacity: 0.4;
 transform: translateX(-100%) translateZ(-200px);
 `),d("next",`
 opacity: 0.4;
 transform: translateX(0%) translateZ(-200px);
 `)])])]);function mn(e){const{length:o}=e;return o>1&&(e.push(ot(e[0],0,"append")),e.unshift(ot(e[o-1],o-1,"prepend"))),e}function ot(e,o,a){return rt(e,{key:`carousel-item-duplicate-${o}-${a}`})}function at(e,o,a){return o===1?0:a?e===0?o-3:e===o-1?0:e-1:e}function Te(e,o){return o?e+1:e}function xn(e,o,a){return e<0?null:e===0?a?o-1:null:e-1}function wn(e,o,a){return e>o-1?null:e===o-1?a?0:null:e+1}function bn(e,o){return o&&e>3?e-2:e}function st(e){return window.TouchEvent&&e instanceof window.TouchEvent}function lt(e,o){let{offsetWidth:a,offsetHeight:r}=e;if(o){const v=getComputedStyle(e);a=a-Number.parseFloat(v.getPropertyValue("padding-left"))-Number.parseFloat(v.getPropertyValue("padding-right")),r=r-Number.parseFloat(v.getPropertyValue("padding-top"))-Number.parseFloat(v.getPropertyValue("padding-bottom"))}return{width:a,height:r}}function xe(e,o,a){return e<o?o:e>a?a:e}function yn(e){if(e===void 0)return 0;if(typeof e=="number")return e;const o=/^((\d+)?\.?\d+?)(ms|s)?$/,a=e.match(o);if(a){const[,r,,v="ms"]=a;return Number(r)*(v==="ms"?1:1e3)}return 0}const Sn=["transitionDuration","transitionTimingFunction"],Cn=Object.assign(Object.assign({},ut.props),{defaultIndex:{type:Number,default:0},currentIndex:Number,showArrow:Boolean,dotType:{type:String,default:"dot"},dotPlacement:{type:String,default:"bottom"},slidesPerView:{type:[Number,String],default:1},spaceBetween:{type:Number,default:0},centeredSlides:Boolean,direction:{type:String,default:"horizontal"},autoplay:Boolean,interval:{type:Number,default:5e3},loop:{type:Boolean,default:!0},effect:{type:String,default:"slide"},showDots:{type:Boolean,default:!0},trigger:{type:String,default:"click"},transitionStyle:{type:Object,default:()=>({transitionDuration:"300ms"})},transitionProps:Object,draggable:Boolean,prevSlideStyle:[Object,String],nextSlideStyle:[Object,String],touchable:{type:Boolean,default:!0},mousewheel:Boolean,keyboard:Boolean,"onUpdate:currentIndex":Function,onUpdateCurrentIndex:Function});let Ee=!1;const _n=be({name:"Carousel",props:Cn,slots:Object,setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:a}=ye(e),r=N(null),v=N(null),C=N([]),P={value:[]},_=m(()=>e.direction==="vertical"),z=m(()=>_.value?"height":"width"),x=m(()=>_.value?"bottom":"right"),f=m(()=>e.effect==="slide"),y=m(()=>e.loop&&e.slidesPerView===1&&f.value),I=m(()=>e.effect==="custom"),A=m(()=>!f.value||e.centeredSlides?1:e.slidesPerView),L=m(()=>I.value?1:e.slidesPerView),k=m(()=>A.value==="auto"||e.slidesPerView==="auto"&&e.centeredSlides),h=N({width:0,height:0}),g=N(0),w=m(()=>{const{value:t}=C;if(!t.length)return[];g.value;const{value:n}=k;if(n)return t.map(R=>lt(R));const{value:s}=L,{value:i}=h,{value:u}=z;let l=i[u];if(s!=="auto"){const{spaceBetween:R}=e,$=l-(s-1)*R,ve=1/Math.max(1,s);l=$*ve}const b=Object.assign(Object.assign({},i),{[u]:l});return t.map(()=>b)}),V=m(()=>{const{value:t}=w;if(!t.length)return[];const{centeredSlides:n,spaceBetween:s}=e,{value:i}=z,{[i]:u}=h.value;let l=0;return t.map(({[i]:b})=>{let R=l;return n&&(R+=(b-u)/2),l+=b+s,R})}),se=N(!1),Y=m(()=>{const{transitionStyle:t}=e;return t?Ze(t,Sn):{}}),le=m(()=>I.value?0:yn(Y.value.transitionDuration)),Ae=m(()=>{const{value:t}=C;if(!t.length)return[];const n=!(k.value||L.value===1),s=b=>{if(n){const{value:R}=z;return{[R]:`${w.value[b][R]}px`}}};if(I.value)return t.map((b,R)=>s(R));const{effect:i,spaceBetween:u}=e,{value:l}=x;return t.reduce((b,R,$)=>{const ve=Object.assign(Object.assign({},s($)),{[`margin-${l}`]:`${u}px`});return b.push(ve),se.value&&(i==="fade"||i==="card")&&Object.assign(ve,Y.value),b},[])}),D=m(()=>{const{value:t}=A,{length:n}=C.value;if(t!=="auto")return Math.max(n-t,0)+1;{const{value:s}=w,{length:i}=s;if(!i)return n;const{value:u}=V,{value:l}=z,b=h.value[l];let R=s[s.length-1][l],$=i;for(;$>1&&R<b;)$--,R+=u[$]-u[$-1];return xe($+1,1,i)}}),Se=m(()=>bn(D.value,y.value)),ft=Te(e.defaultIndex,y.value),Ce=N(at(ft,D.value,y.value)),j=Yt(Wt(e,"currentIndex"),Ce),E=m(()=>Te(j.value,y.value));function Q(t){var n,s;t=xe(t,0,D.value-1);const i=at(t,D.value,y.value),{value:u}=j;i!==j.value&&(Ce.value=i,(n=e["onUpdate:currentIndex"])===null||n===void 0||n.call(e,i,u),(s=e.onUpdateCurrentIndex)===null||s===void 0||s.call(e,i,u))}function _e(t=E.value){return xn(t,D.value,e.loop)}function ze(t=E.value){return wn(t,D.value,e.loop)}function vt(t){const n=Z(t);return n!==null&&_e()===n&&D.value>1}function ht(t){const n=Z(t);return n!==null&&ze()===n&&D.value>1}function je(t){return E.value===Z(t)}function pt(t){return j.value===t}function Oe(){return _e()===null}function Le(){return ze()===null}let H=0;function Re(t){const n=xe(Te(t,y.value),0,D.value);(t!==j.value||n!==E.value)&&Q(n)}function ie(){const t=_e();t!==null&&(H=-1,Q(t))}function ee(){const t=ze();t!==null&&(H=1,Q(t))}let M=!1;function gt(){(!M||!y.value)&&ie()}function mt(){(!M||!y.value)&&ee()}let W=0;const ke=N({});function re(t,n=0){ke.value=Object.assign({},Y.value,{transform:_.value?`translateY(${-t}px)`:`translateX(${-t}px)`,transitionDuration:`${n}ms`})}function te(t=0){f.value?Pe(E.value,t):W!==0&&(!M&&t>0&&(M=!0),re(W=0,t))}function Pe(t,n){const s=Ue(t);s!==W&&n>0&&(M=!0),W=Ue(E.value),re(s,n)}function Ue(t){let n;return t>=D.value-1?n=Fe():n=V.value[t]||0,n}function Fe(){if(A.value==="auto"){const{value:t}=z,{[t]:n}=h.value,{value:s}=V,i=s[s.length-1];let u;if(i===void 0)u=n;else{const{value:l}=w;u=i+l[l.length-1][t]}return u-n}else{const{value:t}=V;return t[D.value-1]||0}}const ne={currentIndexRef:j,to:Re,prev:gt,next:mt,isVertical:()=>_.value,isHorizontal:()=>!_.value,isPrev:vt,isNext:ht,isActive:je,isPrevDisabled:Oe,isNextDisabled:Le,getSlideIndex:Z,getSlideStyle:bt,addSlide:xt,removeSlide:wt,onCarouselItemClick:yt};rn(ne);function xt(t){t&&C.value.push(t)}function wt(t){if(!t)return;const n=Z(t);n!==-1&&C.value.splice(n,1)}function Z(t){return typeof t=="number"?t:t?C.value.indexOf(t):-1}function bt(t){const n=Z(t);if(n!==-1){const s=[Ae.value[n]],i=ne.isPrev(n),u=ne.isNext(n);return i&&s.push(e.prevSlideStyle||""),u&&s.push(e.nextSlideStyle||""),ct(s)}}let Ie=0,Ne=0,U=0,De=0,ue=!1,Ve=!1;function yt(t,n){let s=!M&&!ue&&!Ve;e.effect==="card"&&s&&!je(t)&&(Re(t),s=!1),s||(n.preventDefault(),n.stopPropagation())}let ce=null;function de(){ce&&(clearInterval(ce),ce=null)}function q(){de(),!e.autoplay||Se.value<2||(ce=window.setInterval(ee,e.interval))}function Xe(t){var n;if(Ee||!(!((n=v.value)===null||n===void 0)&&n.contains(Gt(t))))return;Ee=!0,ue=!0,Ve=!1,De=Date.now(),de(),t.type!=="touchstart"&&!t.target.isContentEditable&&t.preventDefault();const s=st(t)?t.touches[0]:t;_.value?Ne=s.clientY:Ie=s.clientX,e.touchable&&(oe("touchmove",document,fe),oe("touchend",document,G),oe("touchcancel",document,G)),e.draggable&&(oe("mousemove",document,fe),oe("mouseup",document,G))}function fe(t){const{value:n}=_,{value:s}=z,i=st(t)?t.touches[0]:t,u=n?i.clientY-Ne:i.clientX-Ie,l=h.value[s];U=xe(u,-l,l),t.cancelable&&t.preventDefault(),f.value&&re(W-U,0)}function G(){const{value:t}=E;let n=t;if(!M&&U!==0&&f.value){const s=W-U,i=[...V.value.slice(0,D.value-1),Fe()];let u=null;for(let l=0;l<i.length;l++){const b=Math.abs(i[l]-s);if(u!==null&&u<b)break;u=b,n=l}}if(n===t){const s=Date.now()-De,{value:i}=z,u=h.value[i];U>u/2||U/s>.4?ie():(U<-u/2||U/s<-.4)&&ee()}n!==null&&n!==t?(Ve=!0,Q(n),qe(()=>{(!y.value||Ce.value!==j.value)&&te(le.value)})):te(le.value),Ke(),q()}function Ke(){ue&&(Ee=!1),ue=!1,Ie=0,Ne=0,U=0,De=0,ae("touchmove",document,fe),ae("touchend",document,G),ae("touchcancel",document,G),ae("mousemove",document,fe),ae("mouseup",document,G)}function St(){if(f.value&&M){const{value:t}=E;Pe(t,0)}else q();f.value&&(ke.value.transitionDuration="0ms"),M=!1}function Ct(t){if(t.preventDefault(),M)return;let{deltaX:n,deltaY:s}=t;t.shiftKey&&!n&&(n=s);const i=-1,u=1,l=(n||s)>0?u:i;let b=0,R=0;_.value?R=l:b=l;const $=10;(R*s>=$||b*n>=$)&&(l===u&&!Le()?ee():l===i&&!Oe()&&ie())}function _t(){h.value=lt(r.value,!0),q()}function zt(){k.value&&g.value++}function Rt(){e.autoplay&&de()}function kt(){e.autoplay&&q()}Me(()=>{Ht(q),requestAnimationFrame(()=>se.value=!0)}),it(()=>{Ke(),de()}),Zt(()=>{const{value:t}=C,{value:n}=P,s=new Map,i=l=>s.has(l)?s.get(l):-1;let u=!1;for(let l=0;l<t.length;l++){const b=n.findIndex(R=>R.el===t[l]);b!==l&&(u=!0),s.set(t[l],b)}u&&t.sort((l,b)=>i(l)-i(b))}),he(E,(t,n)=>{if(t===n){H=0;return}if(q(),f.value){if(y.value){const{value:s}=D;H===-1&&n===1&&t===s-2?t=0:H===1&&n===s-2&&t===1&&(t=s-1)}Pe(t,le.value)}else te();H=0},{immediate:!0}),he([y,A],()=>{qe(()=>{Q(E.value)})}),he(V,()=>{f.value&&te()},{deep:!0}),he(f,t=>{t?te():(M=!1,re(W=0))});const Pt=m(()=>({onTouchstartPassive:e.touchable?Xe:void 0,onMousedown:e.draggable?Xe:void 0,onWheel:e.mousewheel?Ct:void 0})),It=m(()=>Object.assign(Object.assign({},Ze(ne,["to","prev","next","isPrevDisabled","isNextDisabled"])),{total:Se.value,currentIndex:j.value})),Nt=m(()=>({total:Se.value,currentIndex:j.value,to:ne.to})),Dt={getCurrentIndex:()=>j.value,to:Re,prev:ie,next:ee},Vt=ut("Carousel","-carousel",gn,Jt,e,o),Ye=m(()=>{const{common:{cubicBezierEaseInOut:t},self:{dotSize:n,dotColor:s,dotColorActive:i,dotColorFocus:u,dotLineWidth:l,dotLineWidthActive:b,arrowColor:R}}=Vt.value;return{"--n-bezier":t,"--n-dot-color":s,"--n-dot-color-focus":u,"--n-dot-color-active":i,"--n-dot-size":n,"--n-dot-line-width":l,"--n-dot-line-width-active":b,"--n-arrow-color":R}}),J=a?qt("carousel",void 0,Ye,e):void 0;return Object.assign(Object.assign({mergedClsPrefix:o,selfElRef:r,slidesElRef:v,slideVNodes:P,duplicatedable:y,userWantsControl:I,autoSlideSize:k,realIndex:E,slideStyles:Ae,translateStyle:ke,slidesControlListeners:Pt,handleTransitionEnd:St,handleResize:_t,handleSlideResize:zt,handleMouseenter:Rt,handleMouseleave:kt,isActive:pt,arrowSlotProps:It,dotSlotProps:Nt},Dt),{cssVars:a?void 0:Ye,themeClass:J?.themeClass,onRender:J?.onRender})},render(){var e;const{mergedClsPrefix:o,showArrow:a,userWantsControl:r,slideStyles:v,dotType:C,dotPlacement:P,slidesControlListeners:_,transitionProps:z={},arrowSlotProps:x,dotSlotProps:f,$slots:{default:y,dots:I,arrow:A}}=this,L=y&&Ut(y())||[];let k=zn(L);return k.length||(k=L.map(h=>p(pn,null,{default:()=>rt(h)}))),this.duplicatedable&&(k=mn(k)),this.slideVNodes.value=k,this.autoSlideSize&&(k=k.map(h=>p(We,{onResize:this.handleSlideResize},{default:()=>h}))),(e=this.onRender)===null||e===void 0||e.call(this),p("div",Object.assign({ref:"selfElRef",class:[this.themeClass,`${o}-carousel`,this.direction==="vertical"&&`${o}-carousel--vertical`,this.showArrow&&`${o}-carousel--show-arrow`,`${o}-carousel--${P}`,`${o}-carousel--${this.direction}`,`${o}-carousel--${this.effect}`,r&&`${o}-carousel--usercontrol`],style:this.cssVars},_,{onMouseenter:this.handleMouseenter,onMouseleave:this.handleMouseleave}),p(We,{onResize:this.handleResize},{default:()=>p("div",{ref:"slidesElRef",class:`${o}-carousel__slides`,role:"listbox",style:this.translateStyle,onTransitionend:this.handleTransitionEnd},r?k.map((h,g)=>p("div",{style:v[g],key:g},Ft(p(Kt,Object.assign({},z),{default:()=>h}),[[Xt,this.isActive(g)]]))):k)}),this.showDots&&f.total>1&&He(I,f,()=>[p(vn,{key:C+P,total:f.total,currentIndex:f.currentIndex,dotType:C,trigger:this.trigger,keyboard:this.keyboard})]),a&&He(A,x,()=>[p(dn,null)]))}});function zn(e){return e.reduce((o,a)=>(hn(a)&&o.push(a),o),[])}const Rn={class:"portal-home"},kn={class:"hero-section"},Pn={class:"hero-content-wrapper"},In={class:"hero-carousel"},Nn=["onClick"],Dn=["src"],Vn={class:"banner-info"},$n={class:"custom-dots"},Tn=["onClick"],En={class:"main-container"},Mn={class:"content-section"},Bn={class:"section-header"},An={class:"header-left"},jn={class:"header-right"},On={class:"anime-grid"},Ln={class:"content-section"},Un={class:"section-header"},Fn={class:"header-left"},Xn={class:"header-right"},Kn={class:"anime-grid"},Yn={key:0,class:"loading-state"},Wn={__name:"index",setup(e){const o=en(),a={render:()=>p("svg",{viewBox:"0 0 24 24",fill:"currentColor"},[p("path",{d:"M13.5.67s.74 2.65.74 4.8c0 2.06-1.35 3.73-3.41 3.73-2.07 0-3.63-1.67-3.63-3.73l.03-.36C5.21 7.51 4 10.62 4 14c0 4.42 3.58 8 8 8s8-3.58 8-8C20 8.61 17.41 3.8 13.5.67zM11.71 19c-1.78 0-3.22-1.4-3.22-3.14 0-1.62 1.05-2.76 2.81-3.12 1.77-.36 3.6-1.21 4.62-2.58.39 1.29.59 2.65.59 4.04 0 2.65-2.15 4.8-4.8 4.8z"})])},r={render:()=>p("svg",{viewBox:"0 0 24 24",fill:"currentColor"},[p("path",{d:"M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"})])},v={render:()=>p("svg",{viewBox:"0 0 24 24",fill:"currentColor"},[p("path",{d:"M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"})])},C={render:()=>p("svg",{viewBox:"0 0 24 24",fill:"currentColor"},[p("path",{d:"M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"})])},P=N(!0),_=N(!1),z=N([]),x=N([]),f=N([]),y=N(0),I=m(()=>z.value[y.value]?.imgUrl||"");Me(async()=>{try{const[h,g,w]=await Promise.all([nn.getActiveBanners().catch(()=>({data:[]})),$e.getVideos({pageNo:1,pageSize:12,status:2}).catch(()=>({data:{pageData:[]}})),$e.getVideos({pageNo:1,pageSize:12,status:2}).catch(()=>({data:{pageData:[]}}))]);z.value=h.data||[],z.value.length===0&&(z.value=[{id:1,title:"欢迎来到 VMS 视频站",imgUrl:"https://picsum.photos/1920/600?random=1"},{id:2,title:"探索无限可能",imgUrl:"https://picsum.photos/1920/600?random=2"},{id:3,title:"发现精彩内容",imgUrl:"https://picsum.photos/1920/600?random=3"}]),x.value=g.data?.pageData||[],f.value=w.data?.pageData||[]}finally{P.value=!1}});function A(h){y.value=h}function L(h){h.linkUrl&&(h.linkUrl.startsWith("http")?window.open(h.linkUrl,"_blank"):o.push(h.linkUrl))}async function k(){if(!_.value){_.value=!0;try{const h=Math.floor(Math.random()*5)+1,{data:g}=await $e.getVideos({pageNo:h,pageSize:12,status:2}),w=g?.pageData||[];w.length>0?(x.value=w,window.$message?.success("已为您更换推荐内容")):window.$message?.info("暂无更多推荐内容")}catch{window.$message?.error("刷新失败")}finally{_.value=!1}}}return(h,g)=>(O(),X("div",Rn,[S("section",kn,[S("div",{class:"hero-bg",style:ct({backgroundImage:`url(${I.value})`})},null,4),S("div",Pn,[S("div",In,[T(K(_n),{autoplay:"",interval:5e3,draggable:"",effect:"fade","on-update:current-index":A},{dots:F(({total:w,currentIndex:V,to:se})=>[S("div",$n,[(O(!0),X(pe,null,ge(w,Y=>(O(),X("div",{key:Y,class:Je(["dot",{active:V===Y-1}]),onClick:le=>se(Y-1)},null,10,Tn))),128))])]),default:F(()=>[(O(!0),X(pe,null,ge(z.value,w=>(O(),X("div",{key:w.id,class:"banner-slide",onClick:V=>L(w)},[S("img",{src:w.imgUrl,class:"banner-img"},null,8,Dn),g[2]||(g[2]=S("div",{class:"banner-overlay"},null,-1)),S("div",Vn,[S("h2",null,tn(w.title),1),g[1]||(g[1]=S("p",{class:"banner-desc"},"连载中 · 2024年热门番剧",-1))])],8,Nn))),128))]),_:1})])])]),S("div",En,[S("section",Mn,[S("div",Bn,[S("div",An,[T(K(me),{size:"28",color:"#f07775"},{default:F(()=>[T(a)]),_:1}),g[3]||(g[3]=S("h2",null,"热门推荐",-1))]),S("div",jn,[T(K(Ge),{text:"",class:"refresh-btn",loading:_.value,onClick:k},{icon:F(()=>[T(K(me),{class:Je({"spin-animation":_.value})},{default:F(()=>[T(v)]),_:1},8,["class"])]),default:F(()=>[g[4]||(g[4]=Qe(" 换一换 ",-1))]),_:1},8,["loading"])])]),S("div",On,[(O(!0),X(pe,null,ge(x.value,w=>(O(),et(tt,{key:w.id,video:w},null,8,["video"]))),128))])]),S("section",Ln,[S("div",Un,[S("div",Fn,[T(K(me),{size:"28",color:"#4a90e2"},{default:F(()=>[T(r)]),_:1}),g[5]||(g[5]=S("h2",null,"最新上传",-1))]),S("div",Xn,[T(K(Ge),{text:"",class:"more-btn",onClick:g[0]||(g[0]=w=>h.$router.push("/search"))},{default:F(()=>[g[6]||(g[6]=Qe(" 查看更多 ",-1)),T(K(me),null,{default:F(()=>[T(C)]),_:1})]),_:1})])]),S("div",Kn,[(O(!0),X(pe,null,ge(f.value,w=>(O(),et(tt,{key:w.id,video:w},null,8,["video"]))),128))])]),P.value?(O(),X("div",Yn,[T(K(sn),{size:"large"})])):Qt("",!0)])]))}},no=on(Wn,[["__scopeId","data-v-f00e0131"]]);export{no as default};

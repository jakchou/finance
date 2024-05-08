(function() {
    const loadJSWrap = (arr)=>{
      LazyLoad.js(arr.map(url => (window.UIBasePath || '') + url));
    }
    const loadCSSWrap = (arr)=>{
      LazyLoad.css(arr.map(url => (window.UIBasePath || '') + url));
    }
    const loadAssets = () => {
      loadJSWrap(['/packages/vue@2/dist/vue.min.js','/packages/cloud-ui.vusion@0.22.0/dist-theme/index.js','/packages/@lcap/pc-template@1.2.0/cloudAdminDesigner.umd.min.js','/packages/lcap-login@1.2.3/dist-theme/index.js','/bundle.ccc150ad.min.js']);
      loadCSSWrap(['/packages/cloud-ui.vusion@0.22.0/dist-theme/index.css','/packages/@lcap/pc-template@1.2.0/cloudAdminDesigner.css']);
    }
    
    if(window.ICESTARK && window.ICESTARK.root) {
        Object.assign(window.ICESTARK, {
            appEnter({ container, customProps  }) {
                window.LcapMicro = window.LcapMicro || {};
                Object.assign(window.LcapMicro, {});
            
                if(window.LcapMicro.noAuthUrl && !window.LcapMicro.noAuthFn)
                    window.LcapMicro.noAuthFn = () => {
                        location.href = window.LcapMicro.noAuthUrl;
                    };
            
                if(window.LcapMicro.loginUrl && !window.LcapMicro.loginFn)
                    window.LcapMicro.loginFn = () => {
                        location.href = window.LcapMicro.loginUrl;
                    };

                if(window.LcapMicro.notFoundUrl && !window.LcapMicro.notFoundFn)
                    window.LcapMicro.notFoundFn = () => {
                        location.href = window.LcapMicro.notFoundUrl;
                    };
                
                // 兼容 ICESTARK 旧集成方式
                if(!window.LcapMicro.loginFn)
                    window.LcapMicro.loginFn = window.ICESTARK.loginFn;
                if(!window.LcapMicro.routePrefix)
                    window.LcapMicro.routePrefix = window.ICESTARK.basename;
                if(!window.LcapMicro.proxyPrefix)
                    window.LcapMicro.proxyPrefix = window.ICESTARK.proxyPrefix;

                window.LcapMicro.container = container; 
                window.LcapMicro.props = customProps;
                loadAssets();
            },
            appLeave({ container }) {
                container.innerHTML = null;
                if (window.appVM) {
                    window.appVM.$destroy();
                }
                document.querySelectorAll('script.lazyload').forEach((ele) => {
                    ele.active = false;
                });
            },
        });
    } else
        loadAssets();

})()

goog.require('jsinterop.base.InternalJsUtil');

// monkeypatch the bits of jsinterop-base we use
goog.module.get('jsinterop.base.InternalJsUtil').getIndexed=function(array, index) { return array[index]; };
goog.module.get('jsinterop.base.InternalJsUtil').getLength=function(array) { return array.length; };

// Seems this can't run right away, circular deps haven't been resolved yet.
setTimeout(function(){ FlowChartEntryPoint.$create__().m_onModuleLoad__()}, 0);


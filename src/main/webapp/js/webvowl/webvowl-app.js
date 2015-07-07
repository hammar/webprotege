// Source: src/js/app/header.js
"use strict";

var webvowlApp = webvowlApp || {};
webvowlApp.version = "0.4.0";

// Source: src/js/app/app.js
webvowlApp.app = function (graphContainerSelector, convertedOntology) {

	var app = {},
		graph = webvowl.graph(),
		options = graph.graphOptions(),
		data,
		classCount,
	// Graph modules
		statistics = webvowl.modules.statistics(),
		focuser = webvowl.modules.focuser(),
		datatypeFilter = webvowl.modules.datatypeFilter(),
		subclassFilter = webvowl.modules.subclassFilter(),
		disjointFilter = webvowl.modules.disjointFilter(),
		nodeDegreeFilter = webvowl.modules.nodeDegreeFilter(),
		setOperatorFilter = webvowl.modules.setOperatorFilter(),
		nodeScalingSwitch = webvowl.modules.nodeScalingSwitch(graph),
		compactNotationSwitch = webvowl.modules.compactNotationSwitch(graph),
		pickAndPin = webvowl.modules.pickAndPin(),
		resizeTimeout = 200;

	app.initialize = function () {
		options.graphContainerSelector(graphContainerSelector);
		options.selectionModules().push(focuser);
		options.selectionModules().push(pickAndPin);
		options.filterModules().push(statistics);
		options.filterModules().push(datatypeFilter);
		options.filterModules().push(subclassFilter);
		options.filterModules().push(disjointFilter);
		options.filterModules().push(setOperatorFilter);
		options.filterModules().push(nodeScalingSwitch);
		options.filterModules().push(nodeDegreeFilter);
		options.filterModules().push(compactNotationSwitch);

		graph.start();
		classCount = statistics.classCount();

		adjustSize();
		d3.select(window).on("resize", function(){
			setTimeout(adjustSize, resizeTimeout);
		});

		app.data(convertedOntology);

		return app;
	};
	
	app.getClassCount = function () {
		return statistics.classCount();
	};

	app.data = function (convertedOntology) {
		if (!arguments.length) return data;
		data = convertedOntology;
		updateGraph();

		return app;
	};
	
	function updateGraph() {
		var dataAsJson;
		if (data) {
			dataAsJson = JSON.parse(data);
		}
		options.data(dataAsJson);
		graph.reload();
	}

	function adjustSize() {
		var graphContainer = d3.select(graphContainerSelector),
			svg = graphContainer.select("svg"),
			height = parseInt(graphContainer.style("height"), 10),
			width = parseInt(graphContainer.style("width"), 10);

		svg.attr("width", width)
			.attr("height", height);

		options.width(width)
			.height(height);
		graph.updateStyle();
	}

	return app;
};

webvowlApp.browserWarning = function (graphContainerSelector) {

	var browserWarning = {};

	browserWarning.isSupported = function () {

		var version = getInternetExplorerVersion();
		if (version > 0 && version <= 11) {
			return false;
		}

		return true;
	};

	browserWarning.showError = function () {
		d3.select(graphContainerSelector)
			.append("div").attr("id", "browserCheck")
			.append("span").html("The WebVOWL demo does not work in Internet Explorer. Please use another browser, such as <a href=\"http://www.mozilla.org/firefox/\">Mozilla Firefox</a> or <a href=\"https://www.google.com/chrome/\">Google Chrome</a>, to run the WebVOWL demo.");
		//document.write("<div id=\"browserCheck\">The WebVOWL demo does not work in Internet Explorer. Please use another browser, such as <a href=\"http://www.mozilla.org/firefox/\">Mozilla Firefox</a> or <a href=\"https://www.google.com/chrome/\">Google Chrome</a>, to run the WebVOWL demo.</div>");
		// hiding any additional menus and features
	};

	/* Taken from here: http://stackoverflow.com/a/17907562 */
	function getInternetExplorerVersion() {
		var ua,
			re,
			rv = -1;
		if (navigator.appName === "Microsoft Internet Explorer") {
			ua = navigator.userAgent;
			re = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
			if (re.exec(ua) !== null)
				rv = parseFloat(RegExp.$1);
		} else if (navigator.appName === "Netscape") {
			ua = navigator.userAgent;
			re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
			if (re.exec(ua) !== null)
				rv = parseFloat(RegExp.$1);
		}
		return rv;
	}

	return browserWarning;
};

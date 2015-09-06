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
		statistics,
		ontologyInfo,
	// Graph modules
		focuser = webvowl.modules.focuser(),
		datatypeFilter = webvowl.modules.datatypeFilter(),
		subclassFilter = webvowl.modules.subclassFilter(),
		disjointFilter = webvowl.modules.disjointFilter(),
		nodeDegreeFilter = webvowl.modules.nodeDegreeFilter(),
		setOperatorFilter = webvowl.modules.setOperatorFilter(),
		nodeScalingSwitch = webvowl.modules.nodeScalingSwitch(graph),
		compactNotationSwitch = webvowl.modules.compactNotationSwitch(graph),
		pickAndPin = webvowl.modules.pickAndPin(),
		resizeTimeout = 200,
	// selection
		selectionDetailDisplayer = webvowl.modules.selectionDetailsDisplayer(onSelection),
		selectedLabel,
		selectedNode;

	app.initialize = function () {
		options.graphContainerSelector(graphContainerSelector);
		options.selectionModules().push(focuser);
		options.selectionModules().push(pickAndPin);
		options.selectionModules().push(selectionDetailDisplayer);
		options.filterModules().push(datatypeFilter);
		options.filterModules().push(subclassFilter);
		options.filterModules().push(disjointFilter);
		options.filterModules().push(setOperatorFilter);
		options.filterModules().push(nodeScalingSwitch);
		options.filterModules().push(nodeDegreeFilter);
		options.filterModules().push(compactNotationSwitch);

		graph.start();

		adjustSize();
		d3.select(window).on("resize", function(){
			setTimeout(adjustSize, resizeTimeout);
		});

		app.data(convertedOntology);

		return app;
	};

	app.statistics = function() {
		return statistics;
	};
	
	app.ontologyInfo = function() {
		return ontologyInfo;
	}

	app.selectedLabel = function() {
		return selectedLabel;
	};

	app.selectedNode = function() {
		return selectedNode;
	};

	app.data = function (convertedOntology) {
		if (!arguments.length) return data;
		data = convertedOntology;
		updateGraph();

		// use the statistics delievered from the converter
		statistics = options.data().metrics;
		ontologyInfo = options.data().header;
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

	function onSelection(selectedElement) {
		// Click event was prevented when dragging
		if (d3.event && d3.event.defaultPrevented) {
			return;
		}

		// reset selected elements
		selectedLabel = null;
		selectedNode = null;

		// in case of selection, set the correct element
		if (selectedElement instanceof webvowl.labels.BaseLabel) {
			selectedLabel = selectedElement;
		} else if (selectedElement instanceof webvowl.nodes.BaseNode) {
			selectedNode = selectedElement;
		}
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

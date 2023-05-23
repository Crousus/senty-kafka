import React, { useEffect, useRef, useState } from "react";
import * as d3 from "d3";
import cloud from "d3-cloud";

function Wordcloud({ data }) {
  const ref = useRef();
  const [dimensions, setDimensions] = useState({
    width: 500,
    height: 500,
  });

  const getFontSize = (length) => {
    if (length <= 10) return 40;
    if (length <= 20) return 30;
    if (length <= 30) return 20;
    return 15; // Default font size for more than 30 words
  };

  useEffect(() => {
    setDimensions({
      width: ref.current?.offsetWidth || 500,
      height: ref.current?.offsetWidth || 500,
    });
  }, []);

  useEffect(() => {
    const handleResize = () => {
      setDimensions({
        width: ref.current.offsetWidth,
        height: ref.current.offsetWidth,
      });
    };

    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    // Check if there is an existing svg and if so, remove it
    d3.select(ref.current).select("svg").remove();

    const fontSize = getFontSize(data.length); // Get dynamic font size based on number of words

    const layout = cloud()
      .size([dimensions.width, dimensions.height])
      .words(data.map((d) => ({ text: d })))
      .padding(5)
      .rotate(() => ~~(Math.random() * 2) * 90)
      .fontSize(fontSize) // Use dynamic font size
      .on("end", draw);

    layout.start();

    function draw(words) {
      d3.select(ref.current)
        .append("svg")
        .attr("width", layout.size()[0])
        .attr("height", layout.size()[1])
        .append("g")
        .attr(
          "transform",
          `translate(${layout.size()[0] / 2},${layout.size()[1] / 2})`
        )
        .selectAll("text")
        .data(words)
        .enter()
        .append("text")
        .style("font-size", (d) => `${d.size}px`)
        .style("fill", "#fff")
        .attr("text-anchor", "middle")
        .attr("transform", (d) => `translate(${[d.x, d.y]})rotate(${d.rotate})`)
        .text((d) => d.text);
    }
  }, [data, dimensions]);

  return <div ref={ref} />;
}

export default Wordcloud;

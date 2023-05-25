import React, { useContext, useEffect, useMemo, useRef, useState } from "react";
import * as d3 from "d3";
import cloud from "d3-cloud";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { api } from "~/utils/api";
import { fillerWords } from "~/utils/fillerWords";

const remapComments = (data) => {
  if (Object.keys(data).length === 0 || data.data === undefined) return {};

  console.log("data", data.data);

  const result = {};

  for (const videoId in data.data) {
    result[videoId] = data.data[videoId].reduce((acc, commentObj) => {
      // Split the comment string into words
      const words = commentObj.comment.split(" ");
      // Filter out filler words
      const meaningfulWords = words.filter(
        (word) => !fillerWords.includes(word.toLowerCase())
      );
      // Add the words to the accumulator
      return acc.concat(meaningfulWords);
    }, []);
  }

  return result;
};

function Wordcloud() {
  const { checkedVideos } = useContext(CheckedVideosContext);
  const [latestCommentsData, setLatestCommentsData] = useState({});

  const latestCommentsResponse = api.comments.getLatestComments.useQuery({
    videoIds: checkedVideos,
  });

  const remappedData = useMemo(
    () => remapComments(latestCommentsResponse),
    [latestCommentsResponse]
  );

  useEffect(() => {
    const interval = setInterval(() => {
      latestCommentsResponse.refetch().catch((err) => {
        console.log("latestCommentsResponse err", err);
      });
    }, 5000);

    return () => clearInterval(interval); // clear interval on component unmount
  }, []);

  useEffect(() => {
    if (latestCommentsResponse.data) {
      setLatestCommentsData(latestCommentsResponse.data);
    }
  }, [latestCommentsResponse.data]);

  const ref = useRef();
  const [dimensions, setDimensions] = useState({
    width: 500,
    height: 500,
  });

  useEffect(() => {
    let newCheckedWordcloudData = [];
    checkedVideos.forEach((videoId) => {
      // Update checkedWordcloudData
      if (remappedData[videoId]) {
        remappedData[videoId].forEach((word) => {
          if (!newCheckedWordcloudData.includes(word)) {
            newCheckedWordcloudData.push(word);
          }
        });
      }
    });
    // setCheckedWordcloudData(newCheckedWordcloudData);
  }, [checkedVideos]);

  // console.log("checkedWordcloudData", checkedWordcloudData);

  const getFontSize = (length) => {
    if (length <= 10) return 40;
    if (length <= 20) return 30;
    if (length <= 30) return 20;
    if (length <= 40) return 10;
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
  });

  useEffect(() => {
    // Check if there is an existing svg and if so, remove it
    if (Object.keys(remappedData).length === 0) {
      return;
    }

    d3.select(ref.current).select("svg").remove();

    const allWords = Object.values(remappedData).flat();

    const fontSize = getFontSize(allWords.length); // Get dynamic font size based on number of words

    const layout = cloud()
      .size([dimensions.width, dimensions.height])
      .words(allWords.map((d) => ({ text: d })))
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
  }, [dimensions]);

  if (Object.keys(remappedData).length === 0) {
    return <div>No data</div>;
  }

  return (
    <div>
      <div ref={ref} />
    </div>
  );
}

export default Wordcloud;

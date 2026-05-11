import React from "react";
import { Sequence } from "remotion";
import { SceneDashboard } from "./scenes/SceneDashboard";
import { SceneThemeSwitch } from "./scenes/SceneThemeSwitch";
import { SceneBudgets } from "./scenes/SceneBudgets";
import { SceneTechClosing } from "./scenes/SceneTechClosing";

export const MyComposition: React.FC = () => {
  return (
    <>
      <Sequence durationInFrames={600}>
        <SceneDashboard />
      </Sequence>
      <Sequence from={600} durationInFrames={150}>
        <SceneThemeSwitch />
      </Sequence>
      <Sequence from={750} durationInFrames={240}>
        <SceneBudgets />
      </Sequence>
      <Sequence from={990} durationInFrames={210}>
        <SceneTechClosing />
      </Sequence>
    </>
  );
};

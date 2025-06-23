# Branching Growth Simulation

A fun LibGDX-based visual simulation that generates organic-looking branching patterns using L-systems and procedural growth algorithms.

## What it does

This project creates animated, colorful branching structures that grow across the screen in real-time. Each branch follows L-system rules (specifically dragon curve patterns) to create complex, organic-looking growth patterns. The branches feature:

- **Procedural Growth**: Uses L-system grammar rules to generate branching patterns
- **Dynamic Colors**: Each branch cycles through smooth color transitions
- **Glow Effects**: Custom fragment shaders add atmospheric glow and brightness effects
- **Organic Movement**: Random segment lengths and angles create natural-looking growth
- **Multi-threading**: Concurrent branch updates for smooth performance

## Key Features

- **L-System Generation**: Implements dragon curve L-system rules for fractal-like patterns
- **OpenGL Shaders**: Custom fragment shader with Gaussian blur for glow effects
- **Pixmap Drawing**: Direct pixel manipulation for precise line rendering
- **Configurable Parameters**: Easy tweaking of growth patterns, colors, and visual effects
- **Multi-branch Simulation**: Multiple branches grow simultaneously with different starting positions

## Project Structure

- `BranchSimulation.java` - Main application loop, rendering, and shader management
- `Branch.java` - Individual branch logic, L-system interpretation, and drawing
- `Constants.java` - Configuration parameters for easy tweaking
- `fragment_shader.glsl` - GLSL shader for glow and visual effects

## Educational Purpose

This is a personal learning project exploring:
- L-system algorithms and procedural generation
- OpenGL/LibGDX graphics programming
- Custom shader development
- Multi-threaded rendering techniques
- Mathematical visualization

Feel free to experiment with the constants in `Constants.java` to create different growth patterns and visual effects!

## Requirements

- Java 8+
- LibGDX framework
- OpenGL-compatible graphics card

*Note: This is a hobby project created for learning and experimentation with procedural graphics and L-systems.*


![image](https://github.com/user-attachments/assets/386ddf95-06f0-46da-b23d-b513a3e916ac)


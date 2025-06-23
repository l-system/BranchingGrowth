#version 440 core

// Uniforms
uniform float u_time;               // Time in seconds since the start
uniform float iTime;                // Time for glow pulsation (animated time)
uniform sampler2D u_pixelData;      // Texture for pixel data (Pixmap data)
uniform vec2 u_screenSize;          // Screen size (width, height)
uniform float u_brightness;         // Brightness control

// Constants for easy adjustment
const float GLOW_RADIUS = 8.0;     // Glow radius (in pixels)
const float MIN_GLOW_INTENSITY = 0.2; // Minimum glow intensity
const float MAX_GLOW_INTENSITY = 0.3; // Maximum glow intensity
const float GLOW_FREQUENCY = 2.0; // Frequency of glow pulsing
const float WEIGHT_EXPONENT = 2.0;  // Exponent for Gaussian weight calculation
const float LINE_BRIGHTNESS_MULTIPLIER = 8.0; // Brightness multiplier for lines

out vec4 fragColor;                 // Final output color

// Function to compute the glow based on the texture data using Gaussian blur
vec4 applyGlow(vec4 color, vec2 uv, vec2 screenSize) {
    // Calculate glow intensity in the desired range
    float glowIntensity = MIN_GLOW_INTENSITY + (MAX_GLOW_INTENSITY - MIN_GLOW_INTENSITY) *
    (0.5 * (1.0 + sin(iTime * GLOW_FREQUENCY)));

    vec4 sum = vec4(0.0);  // Initialize glow accumulation
    float totalWeight = 0.0; // For normalizing the glow

    // Apply Gaussian blur by sampling neighboring pixels
    for (float x = -GLOW_RADIUS; x <= GLOW_RADIUS; x++) {
        for (float y = -GLOW_RADIUS; y <= GLOW_RADIUS; y++) {
            vec2 offset = vec2(x, y) / screenSize;     // Offset for the neighboring pixel
            vec4 sampleColor = texture(u_pixelData, uv + offset);  // Sample color from the texture

            // Calculate distance and apply smoothstep falloff for the glow
            float dist = length(vec2(x, y)) / GLOW_RADIUS;
            float glowFactor = smoothstep(0.0, 0.2, dist);  // Smoothstep for falloff

            // Apply Gaussian weight for the blur
            float weight = exp(-(x * x + y * y) / (2.0 * GLOW_RADIUS * GLOW_RADIUS));

            // Combine weight, glowFactor, and sampled color for the final glow effect
            sum += sampleColor * weight * glowFactor;
            totalWeight += weight * glowFactor;  // Track total weight
        }
    }

    // Normalize the glow based on the total weight of the samples
    if (totalWeight > 0.0) {
        sum /= totalWeight; // Avoid division by zero
    }

    // Return the original color plus the additive glow effect, enhanced
    return color + sum * glowIntensity * LINE_BRIGHTNESS_MULTIPLIER; // Brighten lines
}

void main() {
    // Normalize the fragment's pixel coordinates to the texture space
    vec2 texCoords = gl_FragCoord.xy / u_screenSize;

    // Sample the texture using normalized coordinates
    vec4 sampledColor = texture(u_pixelData, texCoords);

    // Apply the glow effect on the sampled color
    fragColor = applyGlow(sampledColor, texCoords, u_screenSize);
}

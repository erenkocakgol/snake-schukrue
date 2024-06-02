package com.kristalsoft.kastenblock;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class SnakeGame {
    private long window;
    private Snake snake;
    private Food food;
    private int width = 800;
    private int height = 600;
    private int gridSize = 20;
    private Random random = new Random();
    private int snakeSpeed = 200; // Snake speed in milliseconds

    public static void main(String[] args) {
        new SnakeGame().run();
    }

    public void run() {
        System.out.println("LWJGL " + Version.getVersion() + " running!");

        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Free the window callbacks and destroy the window
            Callbacks.glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        System.out.println("Initializing GLFW...");
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        System.out.println("GLFW initialized successfully.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, "Yılan Oyunu", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        System.out.println("GLFW window created successfully.");

        // Center the window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);

        System.out.println("OpenGL context and window setup complete.");

        // Set up keyboard input
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_UP:
                        snake.setDirection(0, -1);
                        break;
                    case GLFW_KEY_DOWN:
                        snake.setDirection(0, 1);
                        break;
                    case GLFW_KEY_LEFT:
                        snake.setDirection(-1, 0);
                        break;
                    case GLFW_KEY_RIGHT:
                        snake.setDirection(1, 0);
                        break;
                }
            }
        });

        // Initialize the snake and food
        int initialX = width / (2 * gridSize);
        int initialY = height / (2 * gridSize);
        System.out.println("Initial snake position: (" + initialX + ", " + initialY + ")");
        snake = new Snake(initialX, initialY);
        food = new Food(random.nextInt(width / gridSize), random.nextInt(height / gridSize));

        System.out.println("Snake and food initialized. Initial food position: (" + food.getX() + ", " + food.getY() + ")");
    }

    private void loop() {
        System.out.println("Entering main loop...");
        GL.createCapabilities();

        // Set the background color to a dark gray
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        // Disable depth testing for 2D rendering
        glDisable(GL_DEPTH_TEST);

        // Set up 2D orthographic projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            // Draw the snake
            glColor3f(0.0f, 1.0f, 0.0f); // Green color for the snake
            for (int[] part : snake.getBody()) {
                drawSquare(part[0], part[1]);
            }

            // Draw the food
            glColor3f(1.0f, 0.0f, 0.0f); // Red color for the food
            drawSquare(food.getX(), food.getY());

            // Swap buffers to show the current frame
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();

            // Move the snake
            snake.move();

            // Check if the snake has eaten the food
            int[] head = snake.getBody().getFirst();
            if (head[0] == food.getX() && head[1] == food.getY()) {
                snake.grow();
                food.relocate(random.nextInt(width / gridSize), random.nextInt(height / gridSize));
                System.out.println("Snake ate food. New food position: (" + food.getX() + ", " + food.getY() + ")");
            }

            // Check for collisions
            if (snake.checkCollision(width / gridSize, height / gridSize)) {
                System.out.println("Game Over! Snake collided at position: (" + head[0] + ", " + head[1] + ")");
                glfwSetWindowShouldClose(window, true);
            }

            // Add a delay to control the speed of the snake
            try {
                Thread.sleep(snakeSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Exiting main loop...");
    }

    private void drawSquare(int x, int y) {
        int size = gridSize;
        glBegin(GL_QUADS);
        glVertex2f(x * size, y * size);
        glVertex2f(x * size + size, y * size);
        glVertex2f(x * size + size, y * size + size);
        glVertex2f(x * size, y * size + size);
        glEnd();
    }
}
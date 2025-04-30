package com.green.yp.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BinaryTree<T extends Comparable<T>> {

  Node<T> root;

  public void add(T value) {
    root = addRecursive(root, value);
  }

  private Node<T> addRecursive(Node<T> current, T value) {

    if (current == null) {
      return new Node<T>(value);
    }

    if (value.compareTo(current.value) < 0) {
      current.left = addRecursive(current.left, value);
    } else if (value.compareTo(current.value) > 0) {
      current.right = addRecursive(current.right, value);
    }
    return current;
  }

  public boolean isEmpty() {
    return root == null;
  }

  public int getSize() {
    return getSizeRecursive(root);
  }

  private int getSizeRecursive(Node<T> current) {
    return current == null
        ? 0
        : getSizeRecursive(current.left) + 1 + getSizeRecursive(current.right);
  }

  public boolean containsNode(T value) {
    return containsNodeRecursive(root, value);
  }

  private boolean containsNodeRecursive(Node<T> current, T value) {
    if (current == null) {
      return false;
    }

    if (value == current.value) {
      return true;
    }

    return value.compareTo(current.value) < 0
        ? containsNodeRecursive(current.left, value)
        : containsNodeRecursive(current.right, value);
  }

  public void delete(T value) {
    root = deleteRecursive(root, value);
  }

  private Node<T> deleteRecursive(Node<T> current, T value) {
    if (current == null) {
      return null;
    }

    if (value == current.value) {
      // Case 1: no children
      if (current.left == null && current.right == null) {
        return null;
      }

      // Case 2: only 1 child
      if (current.right == null) {
        return current.left;
      }

      if (current.left == null) {
        return current.right;
      }

      // Case 3: 2 children
      T smallestValue = findSmallestValue(current.right);
      current.value = smallestValue;
      current.right = deleteRecursive(current.right, smallestValue);
      return current;
    }
    if (value.compareTo(current.value) < 0) {
      current.left = deleteRecursive(current.left, value);
      return current;
    }

    current.right = deleteRecursive(current.right, value);
    return current;
  }

  private T findSmallestValue(Node<T> root) {
    return root.left == null ? root.value : findSmallestValue(root.left);
  }

  public void traverseInOrder(Node<T> node) {
    if (node != null) {
      traverseInOrder(node.left);
      visit(node.value);
      traverseInOrder(node.right);
    }
  }

  public void traversePreOrder(Node<T> node) {
    if (node != null) {
      visit(node.value);
      traversePreOrder(node.left);
      traversePreOrder(node.right);
    }
  }

  public void traversePostOrder(Node<T> node) {
    if (node != null) {
      traversePostOrder(node.left);
      traversePostOrder(node.right);
      visit(node.value);
    }
  }

  public void traverseLevelOrder() {
    if (root == null) {
      return;
    }

    Queue<Node<T>> nodes = new LinkedList<>();
    nodes.add(root);

    while (!nodes.isEmpty()) {

      Node<T> node = nodes.remove();

      System.out.print(" " + node.value);

      if (node.left != null) {
        nodes.add(node.left);
      }

      if (node.right != null) {
        nodes.add(node.right);
      }
    }
  }

  public void traverseInOrderWithoutRecursion() {
    Stack<Node<T>> stack = new Stack<>();
    Node<T> current = root;

    while (current != null || !stack.isEmpty()) {
      while (current != null) {
        stack.push(current);
        current = current.left;
      }

      Node<T> top = stack.pop();
      visit(top.value);
      current = top.right;
    }
  }

  public void traversePreOrderWithoutRecursion() {
    Stack<Node<T>> stack = new Stack<>();
    Node<T> current = root;
    stack.push(root);

    while (current != null && !stack.isEmpty()) {
      current = stack.pop();
      visit(current.value);

      if (current.right != null) stack.push(current.right);

      if (current.left != null) stack.push(current.left);
    }
  }

  public void traversePostOrderWithoutRecursion() {
    Stack<Node<T>> stack = new Stack<>();
    Node<T> prev = root;
    Node<T> current = root;
    stack.push(root);

    while (current != null && !stack.isEmpty()) {
      current = stack.peek();
      boolean hasChild = (current.left != null || current.right != null);
      boolean isPrevLastChild =
          (prev == current.right || (prev == current.left && current.right == null));

      if (!hasChild || isPrevLastChild) {
        current = stack.pop();
        visit(current.value);
        prev = current;
      } else {
        if (current.right != null) {
          stack.push(current.right);
        }
        if (current.left != null) {
          stack.push(current.left);
        }
      }
    }
  }

  private void visit(T value) {
    System.out.print(" " + value);
  }

  class Node<T extends Comparable<T>> {
    T value;
    Node<T> left;
    Node<T> right;

    Node(T value) {
      this.value = value;
      right = null;
      left = null;
    }
  }
}

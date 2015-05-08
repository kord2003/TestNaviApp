package com.example.testnaviapp.matrix;

public class Vector {

    private final float[] matrixArray = new float[9];

    private float x = 0f;
    private float y = 0f;
    private float z = 0f;

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(Vector v) {
        this(v.x, v.y, v.z);
    }

    public Vector(float x, float y, float z) {
        set(x, y, z);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    
    public void get(float[] array) {
        if (array == null || array.length != 3) throw new IllegalArgumentException("get() array must be non-NULL and size of 3");

        array[0] = this.x;
        array[1] = this.y;
        array[2] = this.z;
    }

    
    public void set(Vector v) {
        if (v == null) return;

        // on set()
        set(v.x, v.y, v.z);
    }

    
    public void set(float[] array) {
        if (array == null || array.length != 3) throw new IllegalArgumentException("get() array must be non-NULL and size of 3");

        // on set()
        set(array[0], array[1], array[2]);
    }

    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        Vector v = (Vector) obj;
        return (v.x == this.x && v.y == this.y && v.z == this.z);
    }

    public boolean equals(float x, float y, float z) {
        return (this.x == x && this.y == y && this.z == z);
    }

    public void add(Vector v) {
        if (v == null) return;

        // on add()
        add(v.x, v.y, v.z);
    }

    public void sub(float x, float y, float z) {

        // on add()
        add(-x, -y, -z);
    }

    public void sub(Vector v) {
        if (v == null) return;

        // on add()
        add(-v.x, -v.y, -v.z);
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void mult(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public float length2D() {
        return (float) Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public void norm() {
        // on divide()
        divide(length());
    }

    public void divide(float s) {
        this.x /= s;
        this.y /= s;
        this.z /= s;
    }

    public float dot(Vector v) {
        if (v == null) return 0f;

        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public void cross(Vector u, Vector v) {
        if (v == null || u == null) return;

        float x = u.y * v.z - u.z * v.y;
        float y = u.z * v.x - u.x * v.z;
        float z = u.x * v.y - u.y * v.x;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void prod(Matrix m) {
        if (m == null) return;

        m.get(matrixArray);
        float xTemp = matrixArray[0] * this.x + matrixArray[1] * this.y + matrixArray[2] * this.z;
        float yTemp = matrixArray[3] * this.x + matrixArray[4] * this.y + matrixArray[5] * this.z;
        float zTemp = matrixArray[6] * this.x + matrixArray[7] * this.y + matrixArray[8] * this.z;

        this.x = xTemp;
        this.y = yTemp;
        this.z = zTemp;
    }

    
    @Override
    public String toString() {
        return "<" + this.x + ", " + this.y + ", " + this.z + ">";
    }
}

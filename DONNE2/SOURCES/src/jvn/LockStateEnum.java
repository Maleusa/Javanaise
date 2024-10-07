package jvn;

import java.io.Serializable;

public enum LockStateEnum implements Serializable {
NOREF,
NOLOCK,
READLOCK,
WRITELOCK,
READLOCKCACHED,
WRITELOCKCACHED,
READWRITE,
READWRITECACHED,
}

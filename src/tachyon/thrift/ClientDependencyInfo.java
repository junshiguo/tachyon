/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 * 
 * @generated
 */
package tachyon.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-03-23")
public class ClientDependencyInfo
    implements org.apache.thrift.TBase<ClientDependencyInfo, ClientDependencyInfo._Fields>,
    java.io.Serializable, Cloneable, Comparable<ClientDependencyInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC =
      new org.apache.thrift.protocol.TStruct("ClientDependencyInfo");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC =
      new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I32, (short) 1);
  private static final org.apache.thrift.protocol.TField PARENTS_FIELD_DESC =
      new org.apache.thrift.protocol.TField("parents", org.apache.thrift.protocol.TType.LIST,
          (short) 2);
  private static final org.apache.thrift.protocol.TField CHILDREN_FIELD_DESC =
      new org.apache.thrift.protocol.TField("children", org.apache.thrift.protocol.TType.LIST,
          (short) 3);
  private static final org.apache.thrift.protocol.TField DATA_FIELD_DESC =
      new org.apache.thrift.protocol.TField("data", org.apache.thrift.protocol.TType.LIST,
          (short) 4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes =
      new HashMap<Class<? extends IScheme>, SchemeFactory>();

  static {
    schemes.put(StandardScheme.class, new ClientDependencyInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ClientDependencyInfoTupleSchemeFactory());
  }

  public int id; // required
  public List<Integer> parents; // required
  public List<Integer> children; // required
  public List<ByteBuffer> data; // required

  /**
   * The set of fields this struct contains, along with convenience methods for finding and
   * manipulating them.
   */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short) 1, "id"), PARENTS((short) 2, "parents"), CHILDREN((short) 3,
        "children"), DATA((short) 4, "data");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
        case 1: // ID
          return ID;
        case 2: // PARENTS
          return PARENTS;
        case 3: // CHILDREN
          return CHILDREN;
        case 4: // DATA
          return DATA;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null)
        throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap =
        new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id",
        org.apache.thrift.TFieldRequirementType.DEFAULT,
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.PARENTS,
        new org.apache.thrift.meta_data.FieldMetaData("parents",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
                new org.apache.thrift.meta_data.FieldValueMetaData(
                    org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.CHILDREN,
        new org.apache.thrift.meta_data.FieldMetaData("children",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
                new org.apache.thrift.meta_data.FieldValueMetaData(
                    org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.DATA,
        new org.apache.thrift.meta_data.FieldMetaData("data",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
                new org.apache.thrift.meta_data.FieldValueMetaData(
                    org.apache.thrift.protocol.TType.STRING, true))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ClientDependencyInfo.class,
        metaDataMap);
  }

  public ClientDependencyInfo() {}

  public ClientDependencyInfo(int id, List<Integer> parents, List<Integer> children,
      List<ByteBuffer> data) {
    this();
    this.id = id;
    setIdIsSet(true);
    this.parents = parents;
    this.children = children;
    this.data = data;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ClientDependencyInfo(ClientDependencyInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    if (other.isSetParents()) {
      List<Integer> __this__parents = new ArrayList<Integer>(other.parents);
      this.parents = __this__parents;
    }
    if (other.isSetChildren()) {
      List<Integer> __this__children = new ArrayList<Integer>(other.children);
      this.children = __this__children;
    }
    if (other.isSetData()) {
      List<ByteBuffer> __this__data = new ArrayList<ByteBuffer>(other.data);
      this.data = __this__data;
    }
  }

  public ClientDependencyInfo deepCopy() {
    return new ClientDependencyInfo(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    this.parents = null;
    this.children = null;
    this.data = null;
  }

  public int getId() {
    return this.id;
  }

  public ClientDependencyInfo setId(int id) {
    this.id = id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  public int getParentsSize() {
    return (this.parents == null) ? 0 : this.parents.size();
  }

  public java.util.Iterator<Integer> getParentsIterator() {
    return (this.parents == null) ? null : this.parents.iterator();
  }

  public void addToParents(int elem) {
    if (this.parents == null) {
      this.parents = new ArrayList<Integer>();
    }
    this.parents.add(elem);
  }

  public List<Integer> getParents() {
    return this.parents;
  }

  public ClientDependencyInfo setParents(List<Integer> parents) {
    this.parents = parents;
    return this;
  }

  public void unsetParents() {
    this.parents = null;
  }

  /** Returns true if field parents is set (has been assigned a value) and false otherwise */
  public boolean isSetParents() {
    return this.parents != null;
  }

  public void setParentsIsSet(boolean value) {
    if (!value) {
      this.parents = null;
    }
  }

  public int getChildrenSize() {
    return (this.children == null) ? 0 : this.children.size();
  }

  public java.util.Iterator<Integer> getChildrenIterator() {
    return (this.children == null) ? null : this.children.iterator();
  }

  public void addToChildren(int elem) {
    if (this.children == null) {
      this.children = new ArrayList<Integer>();
    }
    this.children.add(elem);
  }

  public List<Integer> getChildren() {
    return this.children;
  }

  public ClientDependencyInfo setChildren(List<Integer> children) {
    this.children = children;
    return this;
  }

  public void unsetChildren() {
    this.children = null;
  }

  /** Returns true if field children is set (has been assigned a value) and false otherwise */
  public boolean isSetChildren() {
    return this.children != null;
  }

  public void setChildrenIsSet(boolean value) {
    if (!value) {
      this.children = null;
    }
  }

  public int getDataSize() {
    return (this.data == null) ? 0 : this.data.size();
  }

  public java.util.Iterator<ByteBuffer> getDataIterator() {
    return (this.data == null) ? null : this.data.iterator();
  }

  public void addToData(ByteBuffer elem) {
    if (this.data == null) {
      this.data = new ArrayList<ByteBuffer>();
    }
    this.data.add(elem);
  }

  public List<ByteBuffer> getData() {
    return this.data;
  }

  public ClientDependencyInfo setData(List<ByteBuffer> data) {
    this.data = data;
    return this;
  }

  public void unsetData() {
    this.data = null;
  }

  /** Returns true if field data is set (has been assigned a value) and false otherwise */
  public boolean isSetData() {
    return this.data != null;
  }

  public void setDataIsSet(boolean value) {
    if (!value) {
      this.data = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
      case ID:
        if (value == null) {
          unsetId();
        } else {
          setId((Integer) value);
        }
        break;

      case PARENTS:
        if (value == null) {
          unsetParents();
        } else {
          setParents((List<Integer>) value);
        }
        break;

      case CHILDREN:
        if (value == null) {
          unsetChildren();
        } else {
          setChildren((List<Integer>) value);
        }
        break;

      case DATA:
        if (value == null) {
          unsetData();
        } else {
          setData((List<ByteBuffer>) value);
        }
        break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
      case ID:
        return getId();

      case PARENTS:
        return getParents();

      case CHILDREN:
        return getChildren();

      case DATA:
        return getData();

    }
    throw new IllegalStateException();
  }

  /**
   * Returns true if field corresponding to fieldID is set (has been assigned a value) and false
   * otherwise
   */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
      case ID:
        return isSetId();
      case PARENTS:
        return isSetParents();
      case CHILDREN:
        return isSetChildren();
      case DATA:
        return isSetData();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ClientDependencyInfo)
      return this.equals((ClientDependencyInfo) that);
    return false;
  }

  public boolean equals(ClientDependencyInfo that) {
    if (that == null)
      return false;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_parents = true && this.isSetParents();
    boolean that_present_parents = true && that.isSetParents();
    if (this_present_parents || that_present_parents) {
      if (!(this_present_parents && that_present_parents))
        return false;
      if (!this.parents.equals(that.parents))
        return false;
    }

    boolean this_present_children = true && this.isSetChildren();
    boolean that_present_children = true && that.isSetChildren();
    if (this_present_children || that_present_children) {
      if (!(this_present_children && that_present_children))
        return false;
      if (!this.children.equals(that.children))
        return false;
    }

    boolean this_present_data = true && this.isSetData();
    boolean that_present_data = true && that.isSetData();
    if (this_present_data || that_present_data) {
      if (!(this_present_data && that_present_data))
        return false;
      if (!this.data.equals(that.data))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_id = true;
    list.add(present_id);
    if (present_id)
      list.add(id);

    boolean present_parents = true && (isSetParents());
    list.add(present_parents);
    if (present_parents)
      list.add(parents);

    boolean present_children = true && (isSetChildren());
    list.add(present_children);
    if (present_children)
      list.add(children);

    boolean present_data = true && (isSetData());
    list.add(present_data);
    if (present_data)
      list.add(data);

    return list.hashCode();
  }

  @Override
  public int compareTo(ClientDependencyInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetParents()).compareTo(other.isSetParents());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParents()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.parents, other.parents);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetChildren()).compareTo(other.isSetChildren());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetChildren()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.children, other.children);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetData()).compareTo(other.isSetData());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetData()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.data, other.data);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ClientDependencyInfo(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("parents:");
    if (this.parents == null) {
      sb.append("null");
    } else {
      sb.append(this.parents);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("children:");
    if (this.children == null) {
      sb.append("null");
    } else {
      sb.append(this.children);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("data:");
    if (this.data == null) {
      sb.append("null");
    } else {
      sb.append(this.data);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and
      // doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ClientDependencyInfoStandardSchemeFactory implements SchemeFactory {
    public ClientDependencyInfoStandardScheme getScheme() {
      return new ClientDependencyInfoStandardScheme();
    }
  }

  private static class ClientDependencyInfoStandardScheme
      extends StandardScheme<ClientDependencyInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ClientDependencyInfo struct)
        throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true) {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.id = iprot.readI32();
              struct.setIdIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PARENTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list16 = iprot.readListBegin();
                struct.parents = new ArrayList<Integer>(_list16.size);
                int _elem17;
                for (int _i18 = 0; _i18 < _list16.size; ++_i18) {
                  _elem17 = iprot.readI32();
                  struct.parents.add(_elem17);
                }
                iprot.readListEnd();
              }
              struct.setParentsIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CHILDREN
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list19 = iprot.readListBegin();
                struct.children = new ArrayList<Integer>(_list19.size);
                int _elem20;
                for (int _i21 = 0; _i21 < _list19.size; ++_i21) {
                  _elem20 = iprot.readI32();
                  struct.children.add(_elem20);
                }
                iprot.readListEnd();
              }
              struct.setChildrenIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DATA
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list22 = iprot.readListBegin();
                struct.data = new ArrayList<ByteBuffer>(_list22.size);
                ByteBuffer _elem23;
                for (int _i24 = 0; _i24 < _list22.size; ++_i24) {
                  _elem23 = iprot.readBinary();
                  struct.data.add(_elem23);
                }
                iprot.readListEnd();
              }
              struct.setDataIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ClientDependencyInfo struct)
        throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI32(struct.id);
      oprot.writeFieldEnd();
      if (struct.parents != null) {
        oprot.writeFieldBegin(PARENTS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.I32, struct.parents.size()));
          for (int _iter25 : struct.parents) {
            oprot.writeI32(_iter25);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.children != null) {
        oprot.writeFieldBegin(CHILDREN_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.I32, struct.children.size()));
          for (int _iter26 : struct.children) {
            oprot.writeI32(_iter26);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.data != null) {
        oprot.writeFieldBegin(DATA_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.STRING, struct.data.size()));
          for (ByteBuffer _iter27 : struct.data) {
            oprot.writeBinary(_iter27);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ClientDependencyInfoTupleSchemeFactory implements SchemeFactory {
    public ClientDependencyInfoTupleScheme getScheme() {
      return new ClientDependencyInfoTupleScheme();
    }
  }

  private static class ClientDependencyInfoTupleScheme extends TupleScheme<ClientDependencyInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ClientDependencyInfo struct)
        throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetParents()) {
        optionals.set(1);
      }
      if (struct.isSetChildren()) {
        optionals.set(2);
      }
      if (struct.isSetData()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetId()) {
        oprot.writeI32(struct.id);
      }
      if (struct.isSetParents()) {
        {
          oprot.writeI32(struct.parents.size());
          for (int _iter28 : struct.parents) {
            oprot.writeI32(_iter28);
          }
        }
      }
      if (struct.isSetChildren()) {
        {
          oprot.writeI32(struct.children.size());
          for (int _iter29 : struct.children) {
            oprot.writeI32(_iter29);
          }
        }
      }
      if (struct.isSetData()) {
        {
          oprot.writeI32(struct.data.size());
          for (ByteBuffer _iter30 : struct.data) {
            oprot.writeBinary(_iter30);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ClientDependencyInfo struct)
        throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.id = iprot.readI32();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list31 = new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.parents = new ArrayList<Integer>(_list31.size);
          int _elem32;
          for (int _i33 = 0; _i33 < _list31.size; ++_i33) {
            _elem32 = iprot.readI32();
            struct.parents.add(_elem32);
          }
        }
        struct.setParentsIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list34 = new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.children = new ArrayList<Integer>(_list34.size);
          int _elem35;
          for (int _i36 = 0; _i36 < _list34.size; ++_i36) {
            _elem35 = iprot.readI32();
            struct.children.add(_elem35);
          }
        }
        struct.setChildrenIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list37 = new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.data = new ArrayList<ByteBuffer>(_list37.size);
          ByteBuffer _elem38;
          for (int _i39 = 0; _i39 < _list37.size; ++_i39) {
            _elem38 = iprot.readBinary();
            struct.data.add(_elem38);
          }
        }
        struct.setDataIsSet(true);
      }
    }
  }

}


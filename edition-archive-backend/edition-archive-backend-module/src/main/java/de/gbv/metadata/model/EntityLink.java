/*
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * MyCoRe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCoRe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCoRe.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.gbv.metadata.model;

public class EntityLink {

  public enum Type {
    PERSON, PLACE, ORGANIZATION
  }



  public EntityLink(Type type) {
    this.type = type;
  }

  public EntityLink(String label, String mycoreId, Type type) {
    this(type);
    this.label = label;
    this.mycoreId = mycoreId;
  }

  protected String label;

  protected String mycoreId;

  protected Type type;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getMycoreId() {
    return mycoreId;
  }

  public void setMycoreId(String mycoreId) {
    this.mycoreId = mycoreId;
  }

  public Type getType() {
    return type;
  }
}

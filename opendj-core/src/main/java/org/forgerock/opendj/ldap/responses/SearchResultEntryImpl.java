/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2010 Sun Microsystems, Inc.
 * Portions copyright 2012 ForgeRock AS.
 */

package org.forgerock.opendj.ldap.responses;

import java.util.Collection;

import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.AttributeDescription;
import org.forgerock.opendj.ldap.AttributeParser;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.LinkedHashMapEntry;

/**
 * Search result entry implementation.
 */
final class SearchResultEntryImpl extends AbstractResponseImpl<SearchResultEntry> implements
        SearchResultEntry {

    private final Entry entry;

    SearchResultEntryImpl(final Entry entry) {
        this.entry = entry;
    }

    SearchResultEntryImpl(final SearchResultEntry searchResultEntry) {
        super(searchResultEntry);
        this.entry = LinkedHashMapEntry.deepCopyOfEntry(searchResultEntry);
    }

    @Override
    public boolean addAttribute(final Attribute attribute) {
        return entry.addAttribute(attribute);
    }

    @Override
    public boolean addAttribute(final Attribute attribute,
            final Collection<? super ByteString> duplicateValues) {
        return entry.addAttribute(attribute, duplicateValues);
    }

    @Override
    public SearchResultEntry addAttribute(final String attributeDescription, final Object... values) {
        entry.addAttribute(attributeDescription, values);
        return this;
    }

    @Override
    public SearchResultEntry clearAttributes() {
        entry.clearAttributes();
        return this;
    }

    @Override
    public boolean containsAttribute(final Attribute attribute,
            final Collection<? super ByteString> missingValues) {
        return entry.containsAttribute(attribute, missingValues);
    }

    @Override
    public boolean containsAttribute(final String attributeDescription, final Object... values) {
        return entry.containsAttribute(attributeDescription, values);
    }

    @Override
    public boolean equals(final Object object) {
        return entry.equals(object);
    }

    @Override
    public Iterable<Attribute> getAllAttributes() {
        return entry.getAllAttributes();
    }

    @Override
    public Iterable<Attribute> getAllAttributes(final AttributeDescription attributeDescription) {
        return entry.getAllAttributes(attributeDescription);
    }

    @Override
    public Iterable<Attribute> getAllAttributes(final String attributeDescription) {
        return entry.getAllAttributes(attributeDescription);
    }

    @Override
    public Attribute getAttribute(final AttributeDescription attributeDescription) {
        return entry.getAttribute(attributeDescription);
    }

    @Override
    public Attribute getAttribute(final String attributeDescription) {
        return entry.getAttribute(attributeDescription);
    }

    @Override
    public int getAttributeCount() {
        return entry.getAttributeCount();
    }

    @Override
    public DN getName() {
        return entry.getName();
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }

    @Override
    public AttributeParser parseAttribute(final AttributeDescription attributeDescription) {
        return entry.parseAttribute(attributeDescription);
    }

    @Override
    public AttributeParser parseAttribute(final String attributeDescription) {
        return entry.parseAttribute(attributeDescription);
    }

    @Override
    public boolean removeAttribute(final Attribute attribute,
            final Collection<? super ByteString> missingValues) {
        return entry.removeAttribute(attribute, missingValues);
    }

    @Override
    public boolean removeAttribute(final AttributeDescription attributeDescription) {
        return entry.removeAttribute(attributeDescription);
    }

    @Override
    public SearchResultEntry removeAttribute(final String attributeDescription,
            final Object... values) {
        entry.removeAttribute(attributeDescription, values);
        return this;
    }

    @Override
    public boolean replaceAttribute(final Attribute attribute) {
        return entry.replaceAttribute(attribute);
    }

    @Override
    public SearchResultEntry replaceAttribute(final String attributeDescription,
            final Object... values) {
        entry.replaceAttribute(attributeDescription, values);
        return this;
    }

    @Override
    public SearchResultEntry setName(final DN dn) {
        entry.setName(dn);
        return this;
    }

    @Override
    public SearchResultEntry setName(final String dn) {
        entry.setName(dn);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SearchResultEntry(name=");
        builder.append(getName());
        builder.append(", attributes=");
        builder.append(getAllAttributes());
        builder.append(", controls=");
        builder.append(getControls());
        builder.append(")");
        return builder.toString();
    }

    @Override
    SearchResultEntry getThis() {
        return this;
    }

}

/*
 * Copyright (c) 2023, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.graal.python;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess;

import com.oracle.graal.python.builtins.objects.cext.hpy.GraalHPyContext;
import com.oracle.graal.python.builtins.objects.cext.hpy.jni.GraalHPyJNIContext;
import com.oracle.graal.python.runtime.PythonOptions;

public final class JNIFeature implements Feature {
    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        if (!PythonOptions.WITHOUT_JNI) {
            try {
                // {{start jni upcall config}}
                // @formatter:off
                // Checkstyle: stop
                // DO NOT EDIT THIS PART!
                // This part is automatically generated by hpy.tools.autogen.graalpy.autogen_svm_jni_upcall_config
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDup", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxClose", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromInt32t", int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromUInt32t", int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromInt64t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromUInt64t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromSizet", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongFromSsizet", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsInt32t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsUInt32t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsUInt32tMask", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsInt64t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsUInt64t", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsUInt64tMask", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsSizet", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsSsizet", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsVoidPtr", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLongAsDouble", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFloatFromDouble", double.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFloatAsDouble", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBoolFromBool", boolean.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLength", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxNumberCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAdd", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSubtract", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxMultiply", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxMatrixMultiply", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFloorDivide", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTrueDivide", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxRemainder", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDivmod", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxPower", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxNegative", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxPositive", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAbsolute", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInvert", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLshift", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxRshift", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAnd", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxXor", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxOr", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxIndex", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLong", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFloat", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceAdd", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceSubtract", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceMultiply", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceMatrixMultiply", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceFloorDivide", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceTrueDivide", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceRemainder", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlacePower", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceLshift", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceRshift", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceAnd", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceXor", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxInPlaceOr", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCallableCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCallTupleDict", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCall", long.class, long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCallMethod", long.class, long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFatalError", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrSetString", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrSetObject", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrSetFromErrnoWithFilename", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrSetFromErrnoWithFilenameObjects", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrOccurred"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrExceptionMatches", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrNoMemory"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrClear"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrNewException", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrNewExceptionWithDoc", long.class, long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrWarnEx", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxErrWriteUnraisable", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxIsTrue", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeFromSpec", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeGenericNew", long.class, long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGetAttr", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxHasAttr", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxHasAttrs", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetAttr", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetAttrs", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGetItem", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGetItemi", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxContains", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetItem", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetItemi", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDelItem", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDelItemi", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDelItems", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxType", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeCheck", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeGetName", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeIsSubtype", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxIs", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructObject", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructLegacy", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructType", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructLong", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructFloat", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructUnicode", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructTuple", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsStructList", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTypeGetBuiltinShape", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxNew", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxRepr", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxStr", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxASCII", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytes", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxRichCompare", long.class, long.class, int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxRichCompareBool", long.class, long.class, int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxHash", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesSize", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesGETSIZE", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesAsString", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesASSTRING", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesFromString", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBytesFromStringAndSize", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeFromString", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeAsASCIIString", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeAsLatin1String", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeAsUTF8String", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeAsUTF8AndSize", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeFromWideChar", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeDecodeFSDefault", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeDecodeFSDefaultAndSize", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeEncodeFSDefault", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeReadChar", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeDecodeASCII", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeDecodeLatin1", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeFromEncodedObject", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeSubstring", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListNew", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListAppend", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDictCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDictNew"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDictKeys", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDictCopy", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTupleCheck", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSliceUnpack", long.class, long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxImportImportModule", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCapsuleNew", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCapsuleGet", long.class, int.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCapsuleIsValid", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCapsuleSet", long.class, int.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFromPyObject", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxAsPyObject", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListBuilderNew", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListBuilderSet", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListBuilderBuild", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxListBuilderCancel", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTupleBuilderNew", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTupleBuilderSet", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTupleBuilderBuild", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxTupleBuilderCancel", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFieldLoad", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxReenterPythonExecution", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxLeavePythonExecution"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGlobalLoad", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxDump", long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxCompiles", long.class, long.class, int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxEvalCode", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxContextVarNew", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxContextVarSet", long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetCallFunction", long.class, long.class));
                // @formatter:on
                // Checkstyle: resume
                // {{end jni upcall config}}
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("getHPyDebugContext"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("getHPyTraceContext"));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGetItems", long.class, String.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSetItems", long.class, String.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGetAttrs", long.class, String.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxBulkClose", long.class, int.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxUnicodeFromJCharArray", char[].class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxSequenceFromArray", long[].class, boolean.class, boolean.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxContextVarGet", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxFieldStore", long.class, long.class, long.class));
                RuntimeJNIAccess.register(GraalHPyJNIContext.class.getDeclaredMethod("ctxGlobalStore", long.class, long.class));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Could not register method for JNI access!", e);
            }
            try {
                RuntimeJNIAccess.register(GraalHPyContext.class.getDeclaredField("hpyHandleTable"));
                RuntimeJNIAccess.register(GraalHPyContext.class.getDeclaredField("hpyGlobalsTable"));
                RuntimeJNIAccess.register(GraalHPyContext.class.getDeclaredField("nextHandle"));
            } catch (SecurityException | NoSuchFieldException e) {
                throw new RuntimeException("Could not register field for JNI access!", e);
            }
        }
    }
}

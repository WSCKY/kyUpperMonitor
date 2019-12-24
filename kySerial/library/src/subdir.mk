################################################################################
#  subdir.mk
# Created on: Feb 25, 2019
#     Author: kychu
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
./src/serial.c \
./src/uart.c 

OBJS += \
$(BuildPath)/serial.o \
$(BuildPath)/uart.o 

C_DEPS += \
$(BuildPath)/serial.d \
$(BuildPath)/uart.d

OBJ_DIRS = $(sort $(dir $(OBJS)))

# Each subdirectory must supply rules for building sources it contributes
$(BuildPath)/%.o: ./src/%.c | $(OBJ_DIRS)
	@echo ' CC $<'
	@gcc $(INCS) -O0 -g3 -Wall -c -fPIC -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
